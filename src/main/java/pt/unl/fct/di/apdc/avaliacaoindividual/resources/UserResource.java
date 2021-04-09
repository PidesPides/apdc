package pt.unl.fct.di.apdc.avaliacaoindividual.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaoindividual.utils.RegisterData;
import pt.unl.fct.di.apdc.avaliacaoindividual.utils.Roles.RoleTypes;
import pt.unl.fct.di.apdc.avaliacaoindividual.utils.UserData;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	public UserResource() {
	}; // nothing to do here

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {

		LOG.fine("attempt to register user: " + data.username);

		// IF DO VALID REGISTRATION
		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameters").build();
		}
		// ERRO PARA TAMANHO DA PASSWORD
		if (data.password.length() <= 5)
			return Response.status(Status.FORBIDDEN).entity("Your password must have at least 6 characters.").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User doesn not exist.").build();
			} else {
				user = Entity.newBuilder(userKey).set("user_name", data.username)
						.set("user_pass", DigestUtils.sha512Hex(data.password)).set("user_email", data.email)
						.set("user_role", RoleTypes.USER.name()).build();
				txn.add(user);
				LOG.info("User registered " + data.username);
				txn.commit();
				return Response.ok().build();

			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers() {
		List<UserData> users = new LinkedList<>();
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();
		QueryResults<Entity> tasks = datastore.run(query);
		tasks.forEachRemaining(user -> {
			users.add(g.fromJson(g.toJson(user), UserData.class));
		});
		return Response.ok(g.toJson(users)).build();
	}

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String username) {

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		if (user == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			return Response.ok(g.toJson(user)).build();
		}

	}

	@PUT
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("username") String username, UserData data) {

		if (getUser(username) == null)
			return Response.status(Status.NOT_FOUND).build();
		//
		// como verificar que e mesmo este user logged in a mudar a pass?
		//

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if (user == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User doesn not exist.").build();
			} else {
				user = Entity.newBuilder(userKey).set("user_pass", data.password).set("user_email", data.email)
						.set("user_public", data.profilePublic).set("user_landline", data.landlineNumber)
						.set("user_mobile", data.mobileNumber).set("user_addr", data.address)
						.set("user_addr2", data.alternativeAddress).set("user_zone", data.zone).build();
				txn.put(user);
				LOG.info("User updated " + data.username);
				txn.commit();
				return Response.ok().build();

			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}

	}

	@PUT
	@Path("/role/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(@PathParam("username") String username, UserData data) {
		
		if (getUser(username) == null)
			return Response.status(Status.NOT_FOUND).build();
		
	
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity key = datastore.get(userKey);
		
		
		if(key.contains(RoleTypes.USER.name()))
			return Response.status(Status.FORBIDDEN).build();
				
		//else if(key.contains(RoleTypes.GBO.name()) || key.contains(RoleTypes.GA.name()) || key.contains(RoleTypes.GBO.name()))
		
		return Response.ok().build();
		
	}

	@DELETE
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("username") String username, UserData data) {

		if (getUser(username) == null)
			return Response.status(Status.NOT_FOUND).build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		datastore.delete(userKey);

		return Response.ok().build();
	}

}

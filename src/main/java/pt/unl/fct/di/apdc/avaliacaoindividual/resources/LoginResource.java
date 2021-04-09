package pt.unl.fct.di.apdc.avaliacaoindividual.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaoindividual.utils.*;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * Logger Object
	 * 
	 */

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	
	public LoginResource() {
	}; // nothing to do here

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(LoginData data) {
		LOG.fine("Attempt to login user: " + data.username);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		if (user != null) {
			String hashedPWD = user.getString("user_pass");
			if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				AuthToken token = new AuthToken(data.username);
				LOG.info("User '" + data.username + "' logged in succesfully.");
						return Response.ok(g.toJson(token)).build();
			} else {
				LOG.warning("Wrong password for username:" + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		} else {
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	/*
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Logout(LoginData data, AuthToken token) {
		LOG.fine("Attempt to logout user: " + data.username);
			return Response.ok().build();
		
	
	}
	*/
}

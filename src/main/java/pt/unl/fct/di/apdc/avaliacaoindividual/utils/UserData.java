package pt.unl.fct.di.apdc.avaliacaoindividual.utils;

public class UserData {

	public String username;
	public String email;
	public String password;
	public boolean profilePublic;
	public int landlineNumber;
	public int mobileNumber;
	public String address;
	public String alternativeAddress;
	public String zone;

	public UserData() {

	}

	public UserData(String password, String email, boolean profilePublic, int landlineNumber, int mobileNumber, String address,
			String alternativeAddress, String zone) {

		this.password = password;
		this.email = email;
		this.profilePublic = profilePublic;
		this.landlineNumber = landlineNumber;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.alternativeAddress = alternativeAddress;
		this.zone = zone;

	}

	
}

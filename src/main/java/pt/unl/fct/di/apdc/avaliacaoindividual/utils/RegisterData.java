package pt.unl.fct.di.apdc.avaliacaoindividual.utils;



public class RegisterData {
	
	
	public String username;
	public String password;
	public String email;
	public boolean isActive; 
	
	
	public RegisterData() {
		
	}

	
	public RegisterData(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		
	}
	
	//verifica se user e pass nao sao nulas
	
	public boolean validRegistration() {
		return username != null && password != null;
		
	}
	
	

}

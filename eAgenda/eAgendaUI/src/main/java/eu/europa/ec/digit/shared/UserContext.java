package eu.europa.ec.digit.shared;

import java.io.Serializable;
import java.util.Collection;

import eu.europa.ec.digit.eAgenda.User;

public class UserContext implements Serializable {

	private static final long serialVersionUID = 3265625535571665252L;

	public static final String USERCONTEXT = "$userContext";
	public static final String ADMIN = "$admin";
	
	public User user;
	
	public String builtAt; 

	public Collection<String> roles;

	protected UserContext() {}
	
	public UserContext(User user, Collection<String> roles) {
		this.user = user;
		this.roles = roles;
	}

	public boolean isAdmin() {
		return roles != null && roles.contains(ADMIN);
	}

}

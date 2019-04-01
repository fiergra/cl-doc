package eu.europa.ec.digit.eAgenda;

public class User extends AbstractResource {

	private static final long serialVersionUID = 21549982441754892L;

	public String userId;
	public Person person;
	
	public String searchString;


	protected User() {}

	public User(String userId, String emailAddress, Person person) {
		super( (person != null ? person.getDisplayName() : "") + "(" + userId + ")", emailAddress);
		this.userId = userId;
		this.emailAddress = emailAddress;
		this.person = person;
		searchString = userId + " " + (person != null ? person.searchString : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
	
}

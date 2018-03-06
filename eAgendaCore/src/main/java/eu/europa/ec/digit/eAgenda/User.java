package eu.europa.ec.digit.eAgenda;

public class User implements IResource {

	private static final long serialVersionUID = 21549982441754892L;

	public String userId;
	public Person person;
	
	public String searchString;

	protected User() {}

	public User(String userId, Person person) {
		this.userId = userId;
		this.person = person;
		searchString = userId + " " + (person != null ? person.searchString : "");
	}

	@Override
	public String getDisplayName() {
		return userId + " " + (person != null ? person.getDisplayName() : "");
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

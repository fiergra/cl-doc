package eu.europa.ec.digit.eAgenda;

public class Room extends AbstractResource {

	private static final long serialVersionUID = 4314997830055603572L;
	
	public String name;
	public City city;
	public String searchString;
	public String emailAddress;
	
	protected Room() {}
	
	
	public Room(String name, City city) {
		super(name, null);
		this.name = name;
		this.city = city;
		
		searchString = name + " " + city.code;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Room other = (Room) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}

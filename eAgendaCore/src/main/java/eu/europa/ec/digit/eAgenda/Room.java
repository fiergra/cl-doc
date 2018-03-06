package eu.europa.ec.digit.eAgenda;

public class Room implements IResource {

	private static final long serialVersionUID = 4314997830055603572L;
	
	public String name;
	public City city;
	public String searchString;
	
	protected Room() {}
	
	
	public Room(String name, City city) {
		this.name = name;
		this.city = city;
		
		searchString = name + " " + city.code;
	}



	@Override
	public String getDisplayName() {
		return name;
	}
	
	

}

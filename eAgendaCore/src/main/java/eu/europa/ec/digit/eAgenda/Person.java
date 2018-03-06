package eu.europa.ec.digit.eAgenda;

import java.util.Date;

public class Person implements IResource {

	private static final long serialVersionUID = 21549982441754892L;

	public long perId;
	public String sysperNo;
	public String firstName;
	public String lastName;
	public String gender;
	public Date dateOfBirth;
	
	public String searchString;

	protected Person() {}

	@SuppressWarnings("deprecation")
	public Person(long perId, String sysperNo, String firstName, String lastName, String gender, Date dateOfBirth) {
		this.perId = perId;
		this.sysperNo = sysperNo;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		
		searchString = lastName + " " + firstName + " " + perId + " " + dateOfBirth.getDate() +"/" +  + (dateOfBirth.getMonth()+1) + "/" + (1900 + dateOfBirth.getYear());
	}

	@Override
	public String getDisplayName() {
		return firstName + " " + lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (perId ^ (perId >>> 32));
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
		Person other = (Person) obj;
		if (perId != other.perId)
			return false;
		return true;
	}
	
	
	
}

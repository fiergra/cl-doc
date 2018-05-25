package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Appointment implements Serializable {
	private static final long serialVersionUID = -2665822851932873724L;

//	public ObjectId id;
	public String objectId;

	public IResource host;
	public User guest;
	public Room location;
	
	public Date from;
	public Date until;
	
	public AppointmentType type;
	
	public HashMap<String, Serializable> fields;

	public String comment;
	
	public Appointment() {}

	public Appointment(IResource host, User guest, Room location, Date from, Date until, AppointmentType type) {
		this.host = host;
		this.guest = guest;
		this.location = location;
		this.from = from;
		this.until = until != null ? until : (from != null ? new Date(from.getTime() + (type != null ? type.duration : 15L) * 60 * 1000L) : null);
		this.type = type;
	}
	
	public void put(String key, Serializable value) {
		if (fields == null) {
			fields = new HashMap<>();
		}
		fields.put(key, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
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
		Appointment other = (Appointment) obj;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		return true;
	}
	
	
	
}

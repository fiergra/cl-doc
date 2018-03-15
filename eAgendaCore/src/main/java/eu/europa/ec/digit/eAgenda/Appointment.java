package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.bson.types.ObjectId;

public class Appointment implements Serializable {
	private static final long serialVersionUID = -2665822851932873724L;

	public ObjectId id;

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
		this.until = until != null ? until : new Date(from.getTime() + type.duration * 60 * 1000);
		this.type = type;
	}
	
	public void put(String key, Serializable value) {
		if (fields == null) {
			fields = new HashMap<>();
		}
		fields.put(key, value);
	}
	
}

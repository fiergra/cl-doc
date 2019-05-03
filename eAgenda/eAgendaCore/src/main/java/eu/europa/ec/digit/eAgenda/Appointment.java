package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Appointment implements Serializable {
	private static final long serialVersionUID = -2665822851932873724L;

	public String campaignId;
	public String objectId;

	public IResource host;
	public User guest;
	private Room location;
	
	public Date from;
	public Date until;
	
	public AppointmentType type;
	
	public Map<String, Serializable> fields;

	public String comment;
	
//	private String state;
//	private Map<String, String> states;
	
	public Appointment() {}

	public Appointment(Campaign campaign, IResource host, User guest, Room location, Date from, Date until, AppointmentType type) {
		this.campaignId = campaign.objectId;
		this.host = host;
		this.guest = guest;
		this.location = location;
		this.from = from;
		this.until = until != null ? until : (from != null ? new Date(from.getTime() + (type != null ? type.duration : 15L) * 60 * 1000L) : null);
		this.type = type;
		
//		initStates(campaign.workflows);
	}
	
//	private void initStates(Map<String, FiniteStateMachine> workflows) {
//		states = new HashMap<>();
//		workflows.entrySet().forEach(e -> states.put(e.getKey(), e.getValue().initial));
//	}

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
	
	public Room getLocation() {
		return location != null ? location : (host instanceof Room ? (Room)host : null);
	}

	public void setState(String workflowName, String state) {
//		if (states == null) {
//			states = new HashMap<>();
//		}
//		states.put(workflowName, state);
	}

	public String getState(String workflowName, String initial) {
//		String localState = states != null ? states.get(workflowName) : null;
//		if (localState == null) {
//			localState = initial;
//		}
//		return localState;
		return "draft";
	}
	
}

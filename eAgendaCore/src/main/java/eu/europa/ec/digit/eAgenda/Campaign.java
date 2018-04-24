package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

public class Campaign implements Serializable {
	
	private static final long serialVersionUID = -969364133924117581L;
	
	public String name;
	public String description;

	public AppointmentType appointmentType;
	public List<WorkPattern> patterns;
	public ObjectId id;

	public EmailSettings emailSettings;
	
	public Collection<User> owners;

	public boolean published;

	public Campaign() {}
	
	public Campaign(String name, String description, User owner, AppointmentType appointmentType) {
		this.name = name;
		this.description = description;
		this.patterns = new ArrayList<>();
		if (owner != null) {
			addOwner(owner);
		}
		this.appointmentType = appointmentType;
	}

	public void addWorkPattern(WorkPattern workPattern) {
		if (patterns == null) {
			patterns = new ArrayList<>();
		}
		patterns.add(workPattern);
	}

	public List<IResource> assignedResources() {
		return patterns != null ? patterns.stream().filter(p->p.resource != null).map(p -> p.resource).distinct().collect(Collectors.toList()) : null;
	}
	
	public List<WorkPattern> resourcePatterns(IResource resource) {
		return patterns != null ? patterns.stream().filter(p -> p.resource != null && p.resource.equals(resource)).sorted((w1, w2) -> w1.duration().compareTo(w2.duration())).collect(Collectors.toList()) : null;
	}

	public void removeWorkPattern(WorkPattern workPattern) {
		patterns.remove(workPattern);
	}

	public boolean addOwner(User owner) {
		boolean added = false;
		if (owner != null) {
			if (owners == null) {
				owners = new HashSet<>();
			}
			added = owners.add(owner);
		}
		return added;
	}
	
	public boolean removeOwner(User owner) {
		boolean removed = false; 
		
		if (owners != null) {
			removed = owners.remove(owner);
		}
		
		return removed;
	}

//    public ObjectId getId() {
//        return id;
//    }
//
//    public void setId(final ObjectId id) {
//        this.id = id;
//    }
//
}

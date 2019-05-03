package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Campaign implements Serializable {
	
	private static final long serialVersionUID = -969364133924117581L;
	
	public String name;
	public String description;

	public AppointmentType appointmentType;
	public List<WorkPattern> patterns;
	public String objectId;

	public EmailSettings emailSettings = new EmailSettings();
	
	public Map<String, Collection<User>> roles = new HashMap<>();
	public Collection<User> owners;

	public boolean allowDelegation;

	public boolean published;

	public int startDelayInH = 24;
	
//	public Map<String, FiniteStateMachine> workflows;

	public Campaign() {
		initWorkflows();
	}
	
	public Campaign(String name, String description, AppointmentType appointmentType) {
		this.name = name;
		this.description = description;
		this.patterns = new ArrayList<>();
		this.appointmentType = appointmentType;
		
		initWorkflows();
		
	}

	private void initWorkflows() {
//		if (workflows == null) {
//			workflows = new HashMap<>();
//			workflows.put("presence", new FiniteStateMachine("invited", 
//					new HashSet<>(Arrays.asList("present")), 
//					new FiniteStateMachine.FSMTransition("invited", "show_up", "present"), 
//					new FiniteStateMachine.FSMTransition("present", "revert", "invited")));
//			workflows.put("email", new FiniteStateMachine("sent", 
//					new HashSet<>(Arrays.asList("accepted", "declined")), 
//					new FiniteStateMachine.FSMTransition("sent", "accept", "accepted"), 
//					new FiniteStateMachine.FSMTransition("sent", "decline", "declined")));
//		}
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

	public boolean addRole(String role, User assignee) {
		Collection<User> assignees = roles.get(role);
		if (assignees == null) {
			assignees = new HashSet<>();
			roles.put(role, assignees);
		}
		return assignees.add(assignee);
	}

	public boolean removeRole(String role, User assignee) {
		return roles.get(role) != null ? roles.get(role).remove(assignee) : false;
	}

}

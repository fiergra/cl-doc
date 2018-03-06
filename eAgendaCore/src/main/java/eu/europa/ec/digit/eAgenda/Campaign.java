package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class Campaign implements Serializable {
	
	private static final long serialVersionUID = -969364133924117581L;
	
	public String name;
	public String description;

	public List<WorkPattern> patterns;
	public ObjectId id;

	public boolean published;

	public Campaign() {}
	
	public Campaign(String name, String description) {
		this.name = name;
		this.description = description;
		this.patterns = new ArrayList<>();
	}

	public void addWorkPattern(WorkPattern workPattern) {
		if (patterns == null) {
			patterns = new ArrayList<>();
		}
		patterns.add(workPattern);
	}
	
}

package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;

@Entity
public class ValueBag implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Serialized
	private HashMap<String, Object> fields;
	@Transient
	private List<Participation> participations;
	private String canonicalName;

	public ValueBag() {
	}

	public ValueBag(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public Long getId() {
		return id;
	}

	public HashMap<String, Object> getFields() {
		return fields;
	}
	
	public void addParticipant(RealWorldEntity entity) {
		if (participations == null) {
			participations = new ArrayList<Participation>();
		}
		Participation p = new Participation(this, entity);
		participations.add(p);
	}

	// public HashMap<String, String> getStrings() {
	// return strings;
	// }
	//
	// public HashMap<String, Date> getDates() {
	// return dates;
	// }
	//
	// public HashMap<String, Number> getNumbers() {
	// return numbers;
	// }


	public ValueBag getValueBag(String fieldName) {
		return (ValueBag)get(fieldName);
	}

	public Object get(String fieldName) {
		if (fields == null) { return null; }
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			ValueBag child = getValueBag(fieldName.substring(0, index));
			return child != null ? child.getString(fieldName
					.substring(index + 1)) : "";
		} else {
			return fields.get(fieldName);
		}
	}

	public String getString(String fieldName) {
		return (String) get(fieldName);
		// int index = fieldName.indexOf('.');
		//
		// if (index != -1) {
		// ValueBag child = getValueBag(fieldName.substring(0, index));
		// return child != null ? child.getString(fieldName.substring(index +
		// 1)) : "";
		// } else {
		// return strings.get(fieldName);
		// }
	}

	public Number getNumber(String fieldName) {
		return (Number) get(fieldName);
		// int index = fieldName.indexOf('.');
		//
		// if (index != -1) {
		// ValueBag child = getValueBag(fieldName.substring(0, index));
		// return child.getNumber(fieldName.substring(index + 1));
		// } else {
		// return numbers.get(fieldName);
		// }
	}

	public Date getDate(String fieldName) {
		return (Date) get(fieldName);
		// int index = fieldName.indexOf('.');
		//
		// if (index != -1) {
		// ValueBag child = getValueBag(fieldName.substring(0, index));
		// return child.getDate(fieldName.substring(index + 1));
		// } else {
		// return dates.get(fieldName);
		// }
	}

	public void set(String fieldName, Object value) {
		if (fields == null) {
			 fields = new HashMap<String, Object>();
		}
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			String childName = fieldName.substring(0, index);
			ValueBag child = getValueBag(childName);
			if (child == null) {
				child = new ValueBag(null);
				set(childName, child);
			}
			child.set(fieldName.substring(index + 1), value);
		} else {
			fields.put(fieldName, value);
		}

	}

	/*
	 * public void set(String fieldName, String value) { int index =
	 * fieldName.indexOf('.');
	 * 
	 * if (index != -1) { String childName = fieldName.substring(0, index);
	 * ValueBag child = getValueBag(childName); if (child == null) { child = new
	 * ValueBag(null); set(childName, child); }
	 * child.set(fieldName.substring(index + 1), value); } else {
	 * strings.put(fieldName, value); }
	 * 
	 * }
	 * 
	 * public void set(String fieldName, Date value) { int index =
	 * fieldName.indexOf('.');
	 * 
	 * if (index != -1) { ValueBag child = getValueBag(fieldName.substring(0,
	 * index)); child.set(fieldName.substring(index + 1), value); } else {
	 * dates.put(fieldName, value); } }
	 * 
	 * public void set(String fieldName, Number value) { int index =
	 * fieldName.indexOf('.');
	 * 
	 * if (index != -1) { ValueBag child = getValueBag(fieldName.substring(0,
	 * index)); child.set(fieldName.substring(index + 1), value); } else {
	 * numbers.put(fieldName, value); } }
	 * 
	 * public void set(String fieldName, ValueBag value) { int index =
	 * fieldName.indexOf('.');
	 * 
	 * if (index != -1) { ValueBag child = getValueBag(fieldName.substring(0,
	 * index)); child.set(fieldName.substring(index + 1), value); } else {
	 * children.put(fieldName, value); } }
	 */
	public Collection<String> getFieldNames() {
		Collection<String> fieldNames = new ArrayList<String>(fields.keySet());
		//
		// fields.addAll(strings.keySet());
		// fields.addAll(dates.keySet());
		// fields.addAll(numbers.keySet());
		// fields.addAll(children.keySet());
		//
		return fieldNames;
	}

	public List<Participation> getParticipations() {
		return participations;
	}

	public void setParticipations(List<Participation> participations) {
		this.participations = participations;
	}
	
	

}

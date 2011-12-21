package com.ceres.cldoc.shared.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;

@Entity
public class GenericItem extends AbstractNamedValueAccessor {

	private static final long serialVersionUID = 5273635419946319483L;

	@Id
	private Long id;

	@Serialized
	private HashMap<String, IGenericItemField> fields;
	@Transient
	private List<Participation> participations;
	private String className;

	private Date modified;
	private Date created;

	private String layoutDefinition;
	
	public GenericItem() {
	}

	public GenericItem(String canonicalName, String layoutDefinition) {
		this.className = canonicalName;
		this.layoutDefinition = layoutDefinition;
	}


	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GenericItem) ? obj.hashCode() == hashCode() : super.equals(obj);
	}

	@SuppressWarnings("unused")
	@PrePersist
	private void setDates() {
		if (created == null) {
			created = new Date();
		}
		modified = new Date();
	}
	
	public Date getModified() {
		return modified;
	}

	public Date getCreated() {
		return created;
	}

	public String getClassName() {
		return className;
	}

	public Long getId() {
		return id;
	}

	public HashMap<String, IGenericItemField> getFields() {
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


	private IGenericItem getGenericItem(String fieldName) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ceres.cldoc.shared.domain.INamedValueAccessor#get(java.lang.String)
	 */
	@Override
	public IGenericItemField get(String fieldName) {
		if (fields == null) { return null; }
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			IGenericItem child = getGenericItem(fieldName.substring(0, index));
			return child != null ? child.get(fieldName.substring(index + 1)) : null;
		} else {
			return fields.get(fieldName);
		}
	}


	@Override
	public void set(String fieldName, Object value) {
		if (fields == null) {
			 fields = new HashMap<String, IGenericItemField>();
		}
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			String childName = fieldName.substring(0, index);
			IGenericItem child = getGenericItem(childName);
			if (child == null) {
				child = new GenericItem(null, null);
				set(childName, child);
			}
			child.set(fieldName.substring(index + 1), value);
		} else {
			IGenericItemField field = fields.get(fieldName);
			if (field == null) {
				field = new GenericItemField(null, fieldName, 0);
				fields.put(fieldName, field);
			}
			field.setValue(value);
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

	public String getLayoutDefinition() {
		return layoutDefinition;
	}
	
	

}

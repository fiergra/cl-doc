package com.ceres.cldoc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class GenericItem extends AbstractNamedValueAccessor {

	private static final long serialVersionUID = 5273635419946319483L;

	public Long id;
	public HashMap<String, IGenericItemField> fields;
	public Collection<Participation> participations;
	public String className;
	public Date date;

	public GenericItem() {
	}

	public GenericItem(String className) {
		this.className = className;
	}


	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GenericItem) ? obj.hashCode() == hashCode() : super.equals(obj);
	}

	public void addParticipant(AbstractEntity entity, Date start, Date end) {
		if (participations == null) {
			participations = new ArrayList<Participation>();
		}
		Participation p = new Participation(this, entity, start, end);
		participations.add(p);
	}

	private IGenericItem getGenericItem(String fieldName) {
		return null;
	}

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
				child = new GenericItem(null);
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
	
	public Collection<String> getFieldNames() {
		Collection<String> fieldNames = new ArrayList<String>(fields.keySet());
		return fieldNames;
	}

	public void addField(GenericItemField field) {
		if (fields == null) {
			fields = new HashMap<String, IGenericItemField>();
		}
		fields.put(field.getName(), field);
	}

}

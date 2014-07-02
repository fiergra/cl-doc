package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Act extends AbstractNamedValueAccessor {

	private static final long serialVersionUID = 5273635419946319483L;

	public Long id;
	public ActClass actClass;
	public String summary;
	public Date date;

	public HashMap<String, IActField> fields;
	public HashMap<String, Participation> participations;

	public User createdBy;
	public User modifiedBy;

	public boolean isDeleted;

	public Act() {
	}

	public Act(ActClass actClass) {
		this.actClass = actClass;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Participation getParticipation(Catalog role) {
		return participations != null ? participations.get(role.code) : null;
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Act) ? obj.hashCode() == hashCode() : super.equals(obj);
	}

	
	@Override
	public void setParticipant(Entity e, Catalog role) {
		setParticipant(e, role, null, null);
	}
	
	public void setParticipant(Entity e, Catalog role, Date start, Date end) {
		if (participations == null) {
			participations = new HashMap<String, Participation>();
		}
		
		Participation p = participations.get(role.id);
		if (p == null) {
			p = new Participation(this, e, role, start == null ? new Date() : start, end);
		} else {
			p.entity = e;
			p.start = start == null ? new Date() : start;
			p.end = end;
		}
		participations.put(role.code, p);
	}

	private IAct getGenericAct(String fieldName) {
		return null;
	}

	@Override
	public IActField get(String fieldName) {
		if (fields == null) { return null; }
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			IAct child = getGenericAct(fieldName.substring(0, index));
			return child != null ? child.get(fieldName.substring(index + 1)) : null;
		} else {
			return fields.get(fieldName);
		}
	}


	@Override
	public IActField set(String fieldName, Serializable value) {
		if (fields == null) {
			 fields = new HashMap<String, IActField>();
		}
		
		int index = fieldName.indexOf('.');

		if (index != -1) {
			String childName = fieldName.substring(0, index);
			IAct child = getGenericAct(childName);
			if (child == null) {
				child = new Act();
				set(childName, child);
			}
			return child.set(fieldName.substring(index + 1), value);
		} else {
			IActField field = fields.get(fieldName);
			if (field == null && value != null) {
				field = new ActField(null, fieldName, 0);
				fields.put(fieldName, field);
			}
			if (field != null) {
				field.setValue(value);
			}
			
			return field;
		}

	}
	
	public Collection<String> getFieldNames() {
		Collection<String> fieldNames = new ArrayList<String>(fields.keySet());
		return fieldNames;
	}

	public void addField(ActField field) {
		if (fields == null) {
			fields = new HashMap<String, IActField>();
		}
		fields.put(field.getName(), field);
	}

	public String snapshot() {
		return "<snapshot/>";
	}

	@Override
	public Serializable getValue(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getValue() : null;
	}

	@Override
	public CatalogList getCatalogList(String fieldName) {
		return (CatalogList) getValue(fieldName);
	}

	@Override
	public String toString() {
		return "#" + id + ":" + actClass;
	}

	@Override
	public Serializable setValue(String name, Serializable value) {
		return set(name, value);
	}

	public Participation getParticipation(String role) {
		return participations != null ? participations.get(role) : null;
	}

	@Override
	public int size() {
		return fields.size();
	}

	@Override
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return fields.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return fields.containsValue(value);
	}

	@Override
	public Serializable get(Object key) {
		if ("dateFrom".equals(key)) {
			return getDate();
		} else {
			return key instanceof String ? getValue((String) key) : null;
		}
	}

	@Override
	public Serializable put(String key, Serializable value) {
		if ("dateFrom".equals(key)) {
			setDate((Date) value);
			return null;
		} else if ("isDeleted".equals(key)) {
			isDeleted = (Boolean) value;
			return null;
		} else {
			return setValue(key, value);
		}
	}

	@Override
	public Serializable remove(Object key) {
		return fields.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
	}

	@Override
	public void clear() {
		fields.clear();
	}

	@Override
	public Set<String> keySet() {
		return fields.keySet();
	}

	@Override
	public Collection<Serializable> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return null;
	}

}

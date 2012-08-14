package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Act extends AbstractNamedValueAccessor {

	private static final long serialVersionUID = 5273635419946319483L;

	public Long id;
	public String className;
	public Date date;

	public HashMap<String, IActField> fields;
	public Collection<Participation> participations;

	public Act() {
	}

	public Act(String className) {
		this.className = className;
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
	public Participation getParticipation(long role) {
		Participation p = null;
		if (participations != null) {
			Iterator<Participation> iter = participations.iterator();
			
			while (p == null && iter.hasNext()) {
				Participation next = iter.next();
				if (next.role.id.equals(role)) {
					p = next;
				}
			}
		}		
		return p;
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Act) ? obj.hashCode() == hashCode() : super.equals(obj);
	}

	public void addParticipant(Entity entity, Catalog role, Date start, Date end) {
		if (participations == null) {
			participations = new ArrayList<Participation>();
		}
		
		if (start == null) {
			start = new Date();
		}
		Participation p = new Participation(this, entity, role, start, end);
		participations.add(p);
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
				child = new Act(null);
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

	public Serializable getValue(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getValue() : null;
	}

	@Override
	public CatalogList getCatalogList(String fieldName) {
		return (CatalogList) getValue(fieldName);
	}

}

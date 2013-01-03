package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

import com.ceres.cldoc.model.AbstractNamedValueAccessor;
import com.ceres.cldoc.model.ActField;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.Person;

public class PersonWrapper extends AbstractNamedValueAccessor {
	
	private static final long serialVersionUID = 184132632098539674L;
	private final Person humanBeing;
	
	public PersonWrapper(Person humanBeing) {
		this.humanBeing = humanBeing;
	}
	
		@Override
		public IActField set(String fieldName, Serializable value) {
			if (fieldName.equals("firstName")) {
				humanBeing.firstName = (String) value;
			} else if (fieldName.equals("id")) {
				humanBeing.perId = (Long) value;
			} else if (fieldName.equals("lastName")) {
				humanBeing.lastName = (String) value;
			} else if (fieldName.equals("gender")) {
				humanBeing.gender = (Catalog) value;
			} else if (fieldName.equals("dateOfBirth")) {
				humanBeing.dateOfBirth = (Date) value;
			} else if (fieldName.equals("primaryAddress.street")) {
				humanBeing.getPrimaryAddress().street = (String)value;
			} else if (fieldName.equals("primaryAddress.city")) {
				humanBeing.getPrimaryAddress().city = (String)value;
			} else if (fieldName.equals("primaryAddress.number")) {
				humanBeing.getPrimaryAddress().number = (String)value;
			} else if (fieldName.equals("primaryAddress.co")) {
				humanBeing.getPrimaryAddress().co = (String)value;
			} else if (fieldName.equals("primaryAddress.postCode")) {
				humanBeing.getPrimaryAddress().postCode = (String)value;
			} else if (fieldName.equals("primaryAddress.phone")) {
				humanBeing.getPrimaryAddress().phone = (String)value;
			} else if (fieldName.equals("primaryAddress.note")) {
				humanBeing.getPrimaryAddress().note = (String)value;
			}
			return null;
		}
		
		@Override
		public IActField get(String fieldName) {
			if (fieldName.equals("firstName")) {
				return new ActField(fieldName, humanBeing.firstName);
			} else if (fieldName.equals("id")) {
				return new ActField(fieldName, humanBeing.perId);
			} else if (fieldName.equals("lastName")) {
				return new ActField(fieldName, humanBeing.lastName);
			} else if (fieldName.equals("gender")) {
				return new ActField(fieldName, humanBeing.gender);
			} else if (fieldName.equals("dateOfBirth")) {
				return new ActField(fieldName, humanBeing.dateOfBirth);
			} else if (fieldName.equals("primaryAddress.street")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().street);
			} else if (fieldName.equals("primaryAddress.number")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().number);
			} else if (fieldName.equals("primaryAddress.city")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().city);
			} else if (fieldName.equals("primaryAddress.co")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().co);
			} else if (fieldName.equals("primaryAddress.postCode")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().postCode);
			} else if (fieldName.equals("primaryAddress.phone")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().phone);
			} else if (fieldName.equals("primaryAddress.note")) {
				return new ActField(fieldName, humanBeing.getPrimaryAddress().note);
			} else {
				return null;
			}
		}

		@Override
		public Date getDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setDate(Date date) {
			// TODO Auto-generated method stub
			
		}


}

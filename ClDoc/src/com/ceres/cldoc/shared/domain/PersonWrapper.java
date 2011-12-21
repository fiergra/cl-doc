package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

public class PersonWrapper extends AbstractNamedValueAccessor {
	
	private HumanBeing humanBeing;
	
	public PersonWrapper(HumanBeing humanBeing) {
		this.humanBeing = humanBeing;
	}
	
		@Override
		public <T> void set(String fieldName, T value) {
			if (fieldName.equals("firstName")) {
				humanBeing.firstName = (String) value;
			} else if (fieldName.equals("lastName")) {
				humanBeing.lastName = (String) value;
			} else if (fieldName.equals("dateOfBirth")) {
				humanBeing.dateOfBirth = (Date) value;
			} else if (fieldName.equals("primaryAddress.street")) {
				humanBeing.primaryAddress.street = (String)value;
			} else if (fieldName.equals("primaryAddress.city")) {
				humanBeing.primaryAddress.city = (String)value;
			} else if (fieldName.equals("primaryAddress.number")) {
				humanBeing.primaryAddress.number = (String)value;
			} else if (fieldName.equals("primaryAddress.co")) {
				humanBeing.primaryAddress.co = (String)value;
			} else if (fieldName.equals("primaryAddress.postCode")) {
				humanBeing.primaryAddress.postCode = (String)value;
			}
		}
		
		@Override
		public IGenericItemField get(String fieldName) {
			if (fieldName.equals("firstName")) {
				return new GenericItemField(fieldName, humanBeing.firstName);
			} else if (fieldName.equals("lastName")) {
				return new GenericItemField(fieldName, humanBeing.lastName);
			} else if (fieldName.equals("dateOfBirth")) {
				return new GenericItemField(fieldName, humanBeing.dateOfBirth);
			} else if (fieldName.equals("primaryAddress.street")) {
				return new GenericItemField(fieldName, humanBeing.primaryAddress.street);
			} else if (fieldName.equals("primaryAddress.number")) {
				return new GenericItemField(fieldName, humanBeing.primaryAddress.number);
			} else if (fieldName.equals("primaryAddress.city")) {
				return new GenericItemField(fieldName, humanBeing.primaryAddress.city);
			} else if (fieldName.equals("primaryAddress.co")) {
				return new GenericItemField(fieldName, humanBeing.primaryAddress.co);
			} else if (fieldName.equals("primaryAddress.postCode")) {
				return new GenericItemField(fieldName, humanBeing.primaryAddress.postCode);
			} else {
				return null;
			}
		}

}

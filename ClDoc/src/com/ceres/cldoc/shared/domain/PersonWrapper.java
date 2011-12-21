package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

public class PersonWrapper extends AbstractNamedValueAccessor {
	
	private HumanBeing humanBeing;
	
	public PersonWrapper(HumanBeing humanBeing) {
		this.humanBeing = humanBeing;
	}
	
		@Override
		public void set(String fieldName, Serializable value) {
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
		public Object get(String fieldName) {
			if (fieldName.equals("firstName")) {
				return humanBeing.firstName;
			} else if (fieldName.equals("lastName")) {
				return humanBeing.lastName;
			} else if (fieldName.equals("dateOfBirth")) {
				return humanBeing.dateOfBirth;
			} else if (fieldName.equals("primaryAddress.street")) {
				return humanBeing.primaryAddress.street;
			} else if (fieldName.equals("primaryAddress.number")) {
				return humanBeing.primaryAddress.number;
			} else if (fieldName.equals("primaryAddress.city")) {
				return humanBeing.primaryAddress.city;
			} else if (fieldName.equals("primaryAddress.co")) {
				return humanBeing.primaryAddress.co;
			} else if (fieldName.equals("primaryAddress.postCode")) {
				return humanBeing.primaryAddress.postCode;
			} else {
				return null;
			}
		}

}

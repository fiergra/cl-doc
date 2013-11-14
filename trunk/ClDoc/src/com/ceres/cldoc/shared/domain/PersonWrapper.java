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
	private final Person person;

	public Person getPerson() {
		return person;
	}

	public PersonWrapper(Person humanBeing) {
		this.person = humanBeing;
	}

	@Override
	public IActField set(String fieldName, Serializable value) {
		if (fieldName.equals("firstName")) {
			getPerson().firstName = (String) value;
		} else if (fieldName.equals("lastName")) {
			getPerson().lastName = (String) value;
		} else if (fieldName.equals("gender")) {
			getPerson().gender = (Catalog) value;
		} else if (fieldName.equals("dateOfBirth")) {
			getPerson().dateOfBirth = (Date) value;
		} else if (fieldName.equals("primaryAddress.street")) {
			getPerson().getPrimaryAddress().street = (String) value;
		} else if (fieldName.equals("primaryAddress.city")) {
			getPerson().getPrimaryAddress().city = (String) value;
		} else if (fieldName.equals("primaryAddress.number")) {
			getPerson().getPrimaryAddress().number = (String) value;
		} else if (fieldName.equals("primaryAddress.co")) {
			getPerson().getPrimaryAddress().co = (String) value;
		} else if (fieldName.equals("primaryAddress.postCode")) {
			getPerson().getPrimaryAddress().postCode = (String) value;
		} else if (fieldName.equals("primaryAddress.phone")) {
			getPerson().getPrimaryAddress().phone = (String) value;
		} else if (fieldName.equals("primaryAddress.note")) {
			getPerson().getPrimaryAddress().note = (String) value;
		}
		return null;
	}

	@Override
	public IActField get(String fieldName) {
		if (fieldName.equals("firstName")) {
			return new ActField(fieldName, getPerson().firstName);
		} else if (fieldName.equals("lastName")) {
			return new ActField(fieldName, getPerson().lastName);
		} else if (fieldName.equals("gender")) {
			return new ActField(fieldName, getPerson().gender);
		} else if (fieldName.equals("dateOfBirth")) {
			return new ActField(fieldName, getPerson().dateOfBirth);
		} else if (fieldName.equals("primaryAddress.street")) {
			return new ActField(fieldName,
					getPerson().getPrimaryAddress().street);
		} else if (fieldName.equals("primaryAddress.number")) {
			return new ActField(fieldName,
					getPerson().getPrimaryAddress().number);
		} else if (fieldName.equals("primaryAddress.city")) {
			return new ActField(fieldName, getPerson().getPrimaryAddress().city);
		} else if (fieldName.equals("primaryAddress.co")) {
			return new ActField(fieldName, getPerson().getPrimaryAddress().co);
		} else if (fieldName.equals("primaryAddress.postCode")) {
			return new ActField(fieldName,
					getPerson().getPrimaryAddress().postCode);
		} else if (fieldName.equals("primaryAddress.phone")) {
			return new ActField(fieldName,
					getPerson().getPrimaryAddress().phone);
		} else if (fieldName.equals("primaryAddress.note")) {
			return new ActField(fieldName, getPerson().getPrimaryAddress().note);
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

	@Override
	public Serializable getValue(String name) {
		return get(name);
	}

	@Override
	public Serializable setValue(String name, Serializable value) {
		return set(name, value);
	}

}

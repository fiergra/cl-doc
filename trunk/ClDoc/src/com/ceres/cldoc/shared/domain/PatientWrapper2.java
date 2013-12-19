package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

import com.ceres.cldoc.model.ActField;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.Patient;

public class PatientWrapper2 extends PersonWrapper2 {

	private static final long serialVersionUID = -1830238851588717011L;

	public PatientWrapper2(Patient patient) {
		super(patient);
	}
	
	@Override
	public Patient getPerson() {
		return (Patient) super.getPerson();
	}

	@Override
	public IActField set(String fieldName, Serializable value) {
		if (fieldName.equals("id")) {
			getPerson().perId = (Long) value;
		} 
		return super.set(fieldName, value);
	}

	@Override
	public IActField get(String fieldName) {
		if (fieldName.equals("id")) {
			return new ActField(fieldName, getPerson().perId);
		} else {
			return super.get(fieldName);
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

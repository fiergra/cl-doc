package com.ceres.cldoc.shared.domain;

import com.ceres.cldoc.model.Patient;

public class PatientWrapper extends PersonWrapper {

	private static final long serialVersionUID = -1830238851588717011L;

	public PatientWrapper(Patient patient) {
		super(patient);
		put("perId", patient.perId);
	}
	
	@Override
	public Patient unwrap() {
		Patient p = (Patient) super.unwrap();
		p.perId = (Long) get("perId");
		return p;
	}

}

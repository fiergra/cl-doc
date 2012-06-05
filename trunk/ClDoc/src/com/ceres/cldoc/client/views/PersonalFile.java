package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Person;

public class PersonalFile extends EntityFile<Person> {

	public PersonalFile(ClDoc clDoc, Person hb) {
		super(clDoc, hb, new PersonalFileHeader(hb), "CLDOC.PERSONALFILE");
	}
	
}

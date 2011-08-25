package com.ceres.cldoc.shared.domain;

import java.util.Date;

import javax.persistence.PrePersist;

import com.ceres.cldoc.shared.util.Strings;
import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class HumanBeing extends Person {
	private static final long serialVersionUID = 1L;

	public String firstName;
	public String lastName;
	public String maidenName;
	public Date dateOfBirth;

	private transient String transcriptFirstName;
	private transient String transcriptLastName;
	private transient String transcriptMaidenName;
	
	public HumanBeing() {
	}

	@PrePersist
	void updateTranscript() {
		transcriptLastName = Strings.transcribe(lastName); 
		transcriptFirstName = Strings.transcribe(firstName);
		transcriptMaidenName = Strings.transcribe(maidenName);
	}

}

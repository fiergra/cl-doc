package com.ceres.cldoc.client.views;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.PersonWrapper;

public class PersonEditor extends Form <PersonWrapper> {

	public PersonEditor(Session session, final PersonWrapper result, Runnable setModified) {
		super(session, result, setModified);
	}

	public PersonEditor(Session session, final PersonWrapper result) {
		this(session, result, null);
	}

	@Override
	protected void setup() {
		addLine(SRV.c.firstName(), "firstName", Form.DataTypes.FT_STRING, 50, true);
		addLine(SRV.c.lastName(), "lastName", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.birthDate(), "dateOfBirth", Form.DataTypes.FT_DATE);
		addLine(SRV.c.street(), "primaryAddress.street", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.no(), "primaryAddress.number", Form.DataTypes.FT_STRING, 3);
		addLine(SRV.c.co(), "primaryAddress.co", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.postCode(), "primaryAddress.postCode", Form.DataTypes.FT_STRING, 6);
		addLine(SRV.c.city(), "primaryAddress.city", Form.DataTypes.FT_STRING, 50);
	}


}

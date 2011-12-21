package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.PersonWrapper;

public class PersonEditor extends Form <PersonWrapper> {

	public PersonEditor(final PersonWrapper result, Runnable setModified) {
		super(result, setModified);
	}

	public PersonEditor(final PersonWrapper result) {
		this(result, null);
	}

	@Override
	protected void setup() {
		addLine("FirstName", "firstName", Form.DataTypes.FT_STRING, 50, true);
		addLine("LastName", "lastName", Form.DataTypes.FT_STRING, 50);
		addLine("BirthDate", "dateOfBirth", Form.DataTypes.FT_DATE);
		addLine("Street", "primaryAddress.street", Form.DataTypes.FT_STRING, 50);
		addLine("No", "primaryAddress.number", Form.DataTypes.FT_STRING, 3);
		addLine("c/o", "primaryAddress.co", Form.DataTypes.FT_STRING, 50);
		addLine("PostCode", "primaryAddress.postCode", Form.DataTypes.FT_STRING, 6);
		addLine("City", "primaryAddress.city", Form.DataTypes.FT_STRING, 50);
	}


}

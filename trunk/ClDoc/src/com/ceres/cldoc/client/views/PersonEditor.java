package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.ValueBag;
public class PersonEditor extends Form<ValueBag> {

	public PersonEditor(ValueBag result) {
		super(result);
	}

	@Override
	protected void setup() {
		addLine("FirstName", "firstName", Form.DataTypes.String, 50, true);
		addLine("LastName", "lastName", Form.DataTypes.String, 50);
//		addLine("BirthDate", "dateOfBirth", Form.DATE);
		addLine("Street", "primaryAddress.street", Form.DataTypes.String, 50);
		addLine("No", "primaryAddress.number", Form.DataTypes.String, 3);
		addLine("c/o", "primaryAddress.co", Form.DataTypes.String, 50);
		addLine("PostCode", "primaryAddress.postCode", Form.DataTypes.String, 6);
		addLine("City", "primaryAddress.city", Form.DataTypes.String, 50);
	}


}

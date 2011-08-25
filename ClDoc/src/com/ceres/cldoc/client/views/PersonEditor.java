package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.ValueBag;
public class PersonEditor extends Form<ValueBag> {

	public PersonEditor(ValueBag result) {
		super(result);
	}

	@Override
	protected void setup() {
		addLine("FirstName", "firstName", Form.STRING, 50, true);
		addLine("LastName", "lastName", Form.STRING, 50);
//		addLine("BirthDate", "dateOfBirth", Form.DATE);
		addLine("Street", "primaryAddress.street", Form.STRING, 50);
		addLine("No", "primaryAddress.number", Form.STRING, 3);
		addLine("c/o", "primaryAddress.co", Form.STRING, 50);
		addLine("PostCode", "primaryAddress.postCode", Form.STRING, 6);
		addLine("City", "primaryAddress.city", Form.STRING, 50);
	}


}

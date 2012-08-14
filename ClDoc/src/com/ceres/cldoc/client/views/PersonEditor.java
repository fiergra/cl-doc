package com.ceres.cldoc.client.views;

import java.util.HashMap;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PersonWrapper;

public class PersonEditor extends Form <PersonWrapper> {

	private final OnClick<Person> onSave;

	public PersonEditor(ClDoc clDoc, final PersonWrapper result, Runnable setModified, OnClick<Person> onSave) {
		super(clDoc, result, setModified);
		this.onSave = onSave;
	}

	public PersonEditor(ClDoc clDoc, final PersonWrapper result) {
		this(clDoc, result, null, null);
	}

	@Override
	protected void setup() {
		addLine("id", "id", Form.DataTypes.FT_INTEGER, 5);
		addLine(SRV.c.firstName(), "firstName", Form.DataTypes.FT_STRING, 50, true);
		addLine(SRV.c.lastName(), "lastName", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.birthDate(), "dateOfBirth", Form.DataTypes.FT_DATE);
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("parent", "MASTERDATA.GENDER");
		addLine(SRV.c.gender(), "gender", Form.DataTypes.FT_OPTION_SELECTION, attributes );
		addLine(SRV.c.street(), "primaryAddress.street", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.no(), "primaryAddress.number", Form.DataTypes.FT_STRING, 3);
		addLine(SRV.c.co(), "primaryAddress.co", Form.DataTypes.FT_STRING, 50);
		addLine(SRV.c.postCode(), "primaryAddress.postCode", Form.DataTypes.FT_STRING, 6);
		addLine(SRV.c.city(), "primaryAddress.city", Form.DataTypes.FT_STRING, 50);
	}

	public static void editPerson(final ClDoc clDoc, final Person humanBeing) {
		final PersonEditor pe = new PersonEditor(clDoc, new PersonWrapper(humanBeing));
		pe.showModal("PersonEditor", 
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				pe.savePerson(clDoc, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				pe.deletePerson(clDoc, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
			}
		}
		);
	}
	private void savePerson(ClDoc clDoc, Person result) {
		SRV.humanBeingService.save(clDoc.getSession(), result, new DefaultCallback<Person>(clDoc, "save") {

			@Override
			public void onSuccess(Person result) {
				if (onSave != null) {
					onSave.onClick(result);
				}
//				searchBox.setText(result.lastName);
//				doSearch();
			}
		});
		
	}
	
	private void deletePerson(ClDoc clDoc, Person person) {
		SRV.humanBeingService.delete(clDoc.getSession(), person, new DefaultCallback<Void>(clDoc, "deletePerson"){

			@Override
			public void onSuccess(Void result) {
				
			}});
	}

}

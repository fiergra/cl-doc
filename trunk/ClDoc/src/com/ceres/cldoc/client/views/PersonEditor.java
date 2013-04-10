package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Patient;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PatientWrapper;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonEditor extends Form {

	private final OnClick<Person> onSave;
	
	public PersonEditor(ClDoc clDoc, final Person person, Runnable setModified, OnClick<Person> onSave) {
		super(clDoc, wrap(person), setModified);
		this.onSave = onSave;
	}

	public PersonEditor(ClDoc clDoc, final Person person) {
		this(clDoc, person, null, null);
	}

	@Override
	protected void setup() {
		PersonWrapper model = (PersonWrapper) getModel();
		boolean isPatient = model.getPerson() instanceof Patient;
		
		if (isPatient) {
			final Widget id = addLine("id", "id", Form.DataType.FT_INTEGER, 5);
			
			if (this.getModel().getLong("id").equals(0l)) {
				SRV.humanBeingService.getUniqueId(getClDoc().getSession(), new DefaultCallback<Long>(getClDoc(), "get unique id") {
	
					@Override
					public void onSuccess(Long newId) {
						((TextBox)id).setValue(String.valueOf(newId));
					}
				});
			}
		}			
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("width", "20em");
		List<LineDef> lineDefs = new ArrayList<LineDef>();
		lineDefs.add(new LineDef("", "lastName", Form.DataType.FT_STRING, attributes));
		lineDefs.add(new LineDef(SRV.c.firstName(), "firstName", Form.DataType.FT_STRING, attributes));
		addWidgetsAndFields("Name", lineDefs);
		
		lineDefs.clear();
		attributes.put("parent", "MASTERDATA.GENDER");
		lineDefs.add(new LineDef("", "dateOfBirth", Form.DataType.FT_DATE));
		lineDefs.add(new LineDef(SRV.c.gender(), "gender", Form.DataType.FT_OPTION_SELECTION, attributes));
		addWidgetsAndFields(SRV.c.birthDate(), lineDefs);
		
		lineDefs.clear();
		attributes.put("width", "50em");
		lineDefs.add(new LineDef("", "primaryAddress.street", Form.DataType.FT_STRING, attributes));
		lineDefs.add(new LineDef(SRV.c.no(), "primaryAddress.number", Form.DataType.FT_STRING));
		addWidgetsAndFields(SRV.c.street(), lineDefs);

		addLine(SRV.c.co(), "primaryAddress.co", Form.DataType.FT_STRING, 50);
		addLine("fon", "primaryAddress.phone", Form.DataType.FT_STRING, 50);
		addLine("Bemerkung", "primaryAddress.note", Form.DataType.FT_TEXT);

		lineDefs.clear();
		attributes.put("width", "50em");
		lineDefs.add(new LineDef("", "primaryAddress.postCode", Form.DataType.FT_STRING));
		lineDefs.add(new LineDef(SRV.c.city(), "primaryAddress.city", Form.DataType.FT_STRING, attributes));
		addWidgetsAndFields(SRV.c.postCode(), lineDefs);
	}

	public static void editPerson(final ClDoc clDoc, final Person person, final OnClick<Person> onSave) {
		final PersonEditor pe = new PersonEditor(clDoc, person, new Runnable() {
			
			@Override
			public void run() {
			}
		}, onSave);
		pe.setWidth("100%");
		final ScrollPanel sp = new ScrollPanel(pe);
		sp.addStyleName("docform");
		PopupManager.showModal("PersonEditor", sp, 
		new OnClick<PopupPanel>() {
			
			@Override
			public void onClick(final PopupPanel v) {
				v.hide();
				pe.savePerson(clDoc, person);
			}
		},
		new OnClick<PopupPanel>() {
			
			@Override
			public void onClick(PopupPanel v) {
				v.hide();
				pe.deletePerson(clDoc, person);
			}
		}
		);
	}
	private static PersonWrapper wrap(Person person) {
		return person instanceof Patient ? new PatientWrapper((Patient) person) : new PersonWrapper(person);
	}

	private void savePerson(ClDoc clDoc, Person result) {
		fromDialog();
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

package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Person;
import com.ceres.core.IApplication;
import com.ceres.core.IEntity;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

public class HumanBeingListBox extends OnDemandComboBox <Person> implements IAssignedEntitySelector<Person>{
	
	private Catalog role;
	
	public HumanBeingListBox(final IApplication clDoc, final String role) {
		super(clDoc, 
			role == null ?
			new ListRetrievalService<Person>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Person>> callback) {
				SRV.humanBeingService.search(clDoc.getSession(), filter, callback);
			}
		} : new ListRetrievalService<Person>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Person>> callback) {
				SRV.humanBeingService.findByAssignment(clDoc.getSession(), filter, role, callback);
			}
		}, 
		new LabelFunction<Person>() {

			@Override
			public String getLabel(Person person) {
				return person.lastName + ", " + person.firstName;
			}

			@Override
			public String getValue(Person act) {
				return getLabel(act);
			}
		}, 
		
		new OnClick<TextBox>() {

			@Override
			public void onClick(final TextBox txtFilter) {
				new MessageBox(
						"Neu", "Wollen Sie eine neue Person '" + txtFilter.getText() + "' anlegen?", 
						MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION){

							@Override
							protected void onClick(int result) {
								if (result == MessageBox.MB_YES) {
									Person person = new Person();
									person.lastName = txtFilter.getText();
									PersonEditor.editPerson(clDoc, person, new OnClick<Person>() {
										
										@Override
										public void onClick(Person person) {
											txtFilter.setText(person.getName());
//											setSelectedItem((T) person);
										}
									});
								}
							}}.show();
			}
		}
		,new Runnable() {
			
			@Override
			public void run() {
				PersonEditor.editPerson(clDoc, new Person(), null);
			}
		});
		
		SRV.catalogService.getCatalog(clDoc.getSession(), "ROLES." + role , new DefaultCallback<Catalog>(clDoc, "load role catalog") {

			@Override
			public void onResult(Catalog result) {
				HumanBeingListBox.this.role = result;
			}
		});
//		setSize("25em", "2em");
		addStyleName("humanBeingListBox");
	}

	@Override
	public Catalog getRole() {
		return role;
	}

	@Override
	public boolean setSelected(IEntity entity) {
		return super.setSelected((Person) entity);
	}

	
}

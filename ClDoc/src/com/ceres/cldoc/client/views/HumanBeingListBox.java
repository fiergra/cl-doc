package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandChangeListener;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HumanBeingListBox extends OnDemandComboBox <Person> {
	
	private Catalog role;
	
	public HumanBeingListBox(final ClDoc clDoc, final String role, OnDemandChangeListener<Person> changeHandler) {
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
			public String getLabel(Person act) {
				return act.lastName + ", " + act.firstName;
			}

			@Override
			public String getValue(Person act) {
				return getLabel(act);
			}
		}, changeHandler, new Runnable() {
			
			@Override
			public void run() {
				PersonEditor.editPerson(clDoc, new Person());
			}
		});
		
		SRV.catalogService.getCatalog(clDoc.getSession(), "ROLES." + role , new DefaultCallback<Catalog>(clDoc, "load role catalog") {

			@Override
			public void onSuccess(Catalog result) {
				HumanBeingListBox.this.role = result;
			}
		});
		setSize("25em", "2em");
		addStyleName("humanBeingListBox");
	}

	public Catalog getRole() {
		return role;
	}

	
}

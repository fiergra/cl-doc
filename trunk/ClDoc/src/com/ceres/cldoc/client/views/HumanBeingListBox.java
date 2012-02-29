package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HumanBeingListBox extends OnDemandComboBox <Person> {
	
	private List<Person> humanBeings;
	private Person selected;
	private boolean isMandatory = false;
	private Person emptyRecord;

	public HumanBeingListBox(final Session session) {
		super(new ListRetrievalService<Person>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Person>> callback) {
				SRV.humanBeingService.search(session, filter, callback);
			}
		}, new LabelFunction<Person>() {

			@Override
			public String getLabel(Person item) {
				return item.lastName + ", " + item.firstName;
			}

			@Override
			public String getValue(Person item) {
				return getLabel(item);
			}
		});
		setSize("25em", "2em");
		addStyleName("humanBeingListBox");
	}

}

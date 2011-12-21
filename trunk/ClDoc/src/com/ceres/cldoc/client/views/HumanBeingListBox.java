package com.ceres.cldoc.client.views;

import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class HumanBeingListBox extends OnDemandComboBox <HumanBeing> {
	
	private List<HumanBeing> humanBeings;
	private HumanBeing selected;
	private boolean isMandatory = false;
	private HumanBeing emptyRecord;

	public HumanBeingListBox() {
		super(new ListRetrievalService<HumanBeing>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<HumanBeing>> callback) {
				SRV.humanBeingService.search(filter, callback);
			}
		}, new LabelFunction<HumanBeing>() {

			@Override
			public String getLabel(HumanBeing item) {
				return item.lastName + ", " + item.firstName;
			}

			@Override
			public String getValue(HumanBeing item) {
				return getLabel(item);
			}
		});
		setSize("25em", "2em");
		addStyleName("humanBeingListBox");
	}

}

package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;

public class YesNoRadioGroup extends HorizontalPanel {
	private static Integer groupCount = 0;
	private boolean value;
	private final RadioButton rbYes;
	private final RadioButton rbNo;

	public YesNoRadioGroup(String yesLabel, String noLabel) {
		super();
		setSpacing(5);
		synchronized (groupCount) {
			groupCount++;
			rbYes = new RadioButton("YesNoGroup" + groupCount, yesLabel);
			rbNo = new RadioButton("YesNoGroup" + groupCount, noLabel);
		}
		add(rbYes);
		add(rbNo);

		rbYes.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setBoolean(true);
			}
		});
		rbNo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setBoolean(false);
			}
		});

		initValue(value);
	}

	public void setBoolean(boolean value) {
		setValue(value, true);
	}
	
	public void setValue(boolean value, boolean fireEvents) {
		if (value != this.value) {
			initValue(value);
			if (fireEvents) {
				for (ChangeHandler ch:changeHandlers) {
					ch.onChange(null);
				}
			}
		}
	}

	public void initValue(boolean value) {
		this.value = value;
		rbYes.setValue(value);
		rbNo.setValue(!value);
	}

	public boolean getValue() {
		return value;
	}

	private final List<ChangeHandler> changeHandlers = new ArrayList<ChangeHandler>();
	
	public void addChangeHandler(ChangeHandler changeHandler) {
		changeHandlers.add(changeHandler);
	}


}

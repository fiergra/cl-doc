package com.ceres.cldoc.client.views;

import java.util.Date;

import com.ceres.cldoc.client.controls.DateTextBox;
import com.ceres.cldoc.client.views.Form.DataType;
import com.ceres.cldoc.client.views.ValidationStatus.States;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class InteractorLink {
	public final ValidationStatus status;
	public final Interactor interactor;
	public final String name;
	public final Widget widget;
	public final DataType dataType;
	public final boolean isRequired;

	public InteractorLink(Interactor interactor, ValidationStatus status, String name, Widget widget, DataType dataType, boolean isRequired) {
		this.interactor = interactor;
		this.status = status;
		this.name = name;
		this.widget = widget;
		this.dataType = dataType;
		this.isRequired = isRequired;
	}
	
	private boolean validateTextBox() {
		boolean isValid = true;
		TextBoxBase textBox = (TextBoxBase) widget;
		String sValue = textBox.getText();
		
		if (isRequired) {
			isValid = sValue != null && sValue.length() > 0;
			status.set(isValid ? States.valid : States.required);
		} else {
			status.set(States.none);
		}
		
		return isValid;
	}
	
	private boolean validateDateBox() {
		boolean isValid = true;
		DateTextBox textBox = (DateTextBox) widget;
		Date sValue = textBox.getDate();
		
		if (isRequired) {
			isValid = sValue != null;
			status.set(isValid ? States.valid : States.required);
		} else {
			status.set(States.none);
		}
		
		return isValid;
	}
	
	public boolean validate() {
		boolean isValid = true;
		if (widget instanceof DateTextBox) {
			isValid = validateDateBox();
		} else if (widget instanceof TextBoxBase) {
			isValid = validateTextBox();
		} else if (widget instanceof IEntitySelector) {
			isValid = validateEntitySelector();
		}
		return isValid;
	}

	private boolean validateEntitySelector() {
		boolean isValid = true;
		IEntitySelector<?> textBox = (IEntitySelector<?>) widget;
		Object sValue = textBox.getSelected();
		
		if (isRequired) {
			isValid = sValue != null;
			status.set(isValid ? States.valid : States.required);
		} else {
			status.set(States.none);
		}
		
		return isValid;
	}
		

}

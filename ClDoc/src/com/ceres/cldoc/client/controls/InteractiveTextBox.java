package com.ceres.cldoc.client.controls;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

public class InteractiveTextBox extends TextBox implements InteractiveControl {

	private boolean isMandatory;
	private boolean isValid;

	@Override
	public boolean isMandatory() {
		return isMandatory;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	@Override
	public boolean validate() {
		isValid = validate(this); 
		return isValid;
	}

	
	public static boolean validate(TextBoxBase textBox) {
		boolean isValid = false;
		String sValue = textBox.getText();
		if (sValue == null || sValue.length() == 0) {
			textBox.addStyleName("invalidContent");
		} else {
			textBox.removeStyleName("invalidContent");
			isValid = true;
		}

		return isValid;
	}

	@Override
	public void setMandatory() {
		isMandatory = true;
	}
	
	
}

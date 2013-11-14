package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.ceres.cldoc.client.views.Form.DataType;
import com.ceres.cldoc.client.views.ValidationStatus.States;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Person;
import com.ceres.dynamicforms.client.components.DateTextBox;
import com.ceres.dynamicforms.client.components.FloatTextBox;
import com.ceres.dynamicforms.client.components.LongTextBox;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class ClInteractorLink {
	protected final ValidationStatus status;
	protected final ClInteractor interactor;
	protected final String name;
	protected final Widget widget;
	protected final DataType dataType;
	protected final boolean isRequired;
	protected HashMap<String, String> attributes;

	public ClInteractorLink(ClInteractor interactor, ValidationStatus status, String name, Widget widget, DataType dataType, boolean isRequired, HashMap<String, String> attributes) {
		this.interactor = interactor;
		this.status = status;
		this.name = name;
		this.widget = widget;
		this.dataType = dataType;
		this.isRequired = isRequired;
		this.attributes = attributes;
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
		

	protected Serializable getValue() {
		Serializable value = null;
		
		switch (dataType) {
		case FT_TEXT:
		case FT_STRING:
			String sValue = ((TextBoxBase) widget).getText();
			value = sValue != null && sValue.length() > 0 ? sValue : null;
			break;
		case FT_YESNO:
			value = ((YesNoRadioGroup) widget).getValue();
			break;
		case FT_BOOLEAN:
			value = ((CheckBox) widget).getValue();
			break;
		case FT_MULTI_SELECTION:
			value = ((IEntitySelector<CatalogList>) widget).getSelected();
			break;
		case FT_OPTION_SELECTION:
		case FT_LIST_SELECTION:
			value = ((IEntitySelector<Catalog>) widget).getSelected(); 
			break;
		case FT_PARTICIPATION:
			IAssignedEntitySelector<Person> selector = (IAssignedEntitySelector<Person>) widget;
			value = selector.getSelected(); 
			break;
		case FT_HUMANBEING:
			value = ((IEntitySelector<Person>) widget).getSelected(); 
			break;
		case FT_TIME:
		case FT_DATE:
			value = ((DateTextBox) widget).getDate();
			break;
		case FT_PARTICIPATION_TIME:
			value = ((DateTextBox) widget).getDate();
			break;
		case FT_ACTDATE:
			value = ((DateTextBox) widget).getDate();
			break;
		case FT_FLOAT:
			value = ((FloatTextBox) widget).getFloat();
			break;
		case FT_INTEGER:
			value = ((LongTextBox) widget).getLong();
			break;
		}
		return value;
	}
}

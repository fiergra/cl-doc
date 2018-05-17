package com.ceres.dynamicforms.client.components;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class FloatTextBox extends TextBox {

	private Float value = null;
	
	public FloatTextBox() {
		super();
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Float value = parseValue();
				
				if (value == null) {
					addStyleName("invalid");
				} else {
					removeStyleName("invalid");
					setFloat(value);
				}
			}
		});
	}

	public Float parseValue() {
		String sValue = getValue();
		try {
			value = Float.valueOf(sValue);
		} catch (NumberFormatException x) {
			value = null;
		}
		return value;
	}
	
	public Float getFloat() {
		return value;
	}

	public void setFloat(Float value) {
		this.value = value;
		
		setText(value != null ? String.valueOf(value) : null);
	}
	
	
	
}

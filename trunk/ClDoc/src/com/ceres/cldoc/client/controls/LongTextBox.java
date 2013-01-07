package com.ceres.cldoc.client.controls;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class LongTextBox extends TextBox {

	private Long value = null;
	
	public LongTextBox() {
		super();
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Long value = parseValue();
				
				if (value == null) {
					addStyleName("invalid");
				} else {
					removeStyleName("invalid");
					setLong(value);
				}
			}
		});
	}

	public Long parseValue() {
		String sValue = getValue();
		try {
			value = Long.valueOf(sValue);
		} catch (NumberFormatException x) {
			value = null;
		}
		return value;
	}
	
	public Long getLong() {
		return value;
	}

	public void setLong(Long value) {
		this.value = value;
		
		setText(value != null ? String.valueOf(value) : null);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		parseValue();
	}
	
	
	
	
}

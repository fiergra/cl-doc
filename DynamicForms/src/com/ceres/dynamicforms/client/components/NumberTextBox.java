package com.ceres.dynamicforms.client.components;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class NumberTextBox extends TextBox {

	private Number value = null;
	
	public NumberTextBox() {
		super();
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Number value = parseValue();
				
				if (value != null) {
					setLong(value);
				}
			}
		});
	}

	public Number parseValue() {
		String sValue = getValue();
		try {
			value = Integer.valueOf(sValue);
		} catch (NumberFormatException ix) {
			try {
				value = Long.valueOf(sValue);
			} catch (NumberFormatException lx) {
				try {
					value = Float.valueOf(sValue);
				} catch (NumberFormatException fx) {
					try {
						value = Double.valueOf(sValue);
					} catch (NumberFormatException dx) {
						value = null;
					}
				}
			}
		}
		return value;
	}
	
	public Number getLong() {
		return value;
	}

	public void setLong(Number value) {
		this.value = value;
		
		setText(value != null ? String.valueOf(value) : null);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		parseValue();
	}
	
	
	
	
}

package com.ceres.dynamicforms.client.components;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
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
					setNumber(value);
				}
			}
		});
	}

	public Number parseValue() {
		String sValue = getValue();

		try {
			value = nf.parse(sValue.replace(".", "#").replace(",", ".").replace("#", ","));
		} catch (NumberFormatException nfx) {
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
		}
		return value;
	}
	
	public Number getNumber() {
		return value;
	}

	public void setNumber(Number value) {
		this.value = value;
		if (value != null) {
			setText(format(value));
		} else {
			setText(null);
		}
	}

	private NumberFormat nf = NumberFormat.getFormat("#,##0.00");

	public NumberFormat getNumberFormat() {
		return nf;
	}
	
	public void setNumberFormat(NumberFormat nf) {
		this.nf = nf;
	}
	
	private String format(Number value) {
		return nf.format(value).replace(".", "#").replace(",", ".").replace("#", ",");
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		parseValue();
	}
	
	
	
	
}

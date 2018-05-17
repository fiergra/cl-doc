package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.ITranslator;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorWidgetLink;
import com.google.gwt.user.client.ui.ListBox;

public class StringComboBox extends ListBox {
	
	public static class Factory extends AbstractFactory {
		
		public static final String HAS_NULL = "hasNull";
		public static final String VALUES = "values";
		public static final String SELECTED = "selected";

		public Factory(ITranslator tl) {
			super(tl);
		}

		@Override
		public InteractorWidgetLink createLink(Interactor interactor, final String fieldName, HashMap<String, String> attributes) {
			final StringComboBox sBox = new StringComboBox(translator, getStrings(attributes, VALUES), getBoolean(attributes, HAS_NULL));
			
			if (attributes.containsKey(SELECTED)) {
				sBox.setSelected(attributes.get(SELECTED));
			}
			
			InteractorWidgetLink link = new InteractorWidgetLink(interactor, fieldName, sBox, attributes) {
				
				@Override
				public void toDialog(Map<String, Serializable> item) {
					sBox.setSelected((String)item.get(fieldName));
				}
				
				@Override
				public boolean isEmpty() {
					return sBox.getSelectedValue() == null || sBox.getSelectedValue().length() == 0;
				}
				
				@Override
				public void fromDialog(Map<String, Serializable> item) {
					item.put(fieldName, sBox.getSelectedValue());
				}
			};
			return link;
		}
		
	}

	private List<String> strings;
	private boolean hasNull;
	
	public StringComboBox(ITranslator tl, String[] strings, boolean hasNull) {
		this.strings = Arrays.asList(strings);
		this.hasNull = hasNull;
		if (hasNull) {
			addItem("---");
		}
		for (String s:strings) {
			addItem(tl.getLabel(s), s);
		}
	}

	public void setSelected(String s) {
		if (s == null && hasNull) {
			setSelectedIndex(0);
		} else { 
			int index = strings.indexOf(s);
			setSelectedIndex(index);
		}
	}

}

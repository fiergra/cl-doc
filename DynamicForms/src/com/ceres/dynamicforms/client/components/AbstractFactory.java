package com.ceres.dynamicforms.client.components;

import java.util.HashMap;

import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.ITranslator;

public abstract class AbstractFactory<T> implements ILinkFactory<T> {

	protected final ITranslator<T> translator;
	
	public AbstractFactory(ITranslator<T> translator) {
		this.translator = translator;
	}
	
	protected boolean getBoolean(HashMap<String, String> attributes, String name) {
		boolean value = false;
		
		if (attributes != null && attributes.containsKey(name)) {
			value = Boolean.valueOf(attributes.get(name));
		}
		
		return value;
	}

	public String[] getStrings(HashMap<String, String> attributes, String name) {
		String[] value = null;
		
		if (attributes != null && attributes.containsKey(name)) {
			String sValue = attributes.get(name);
			if (sValue != null) {
				value = sValue.split(";");
			}
		}
		
		return value;
	}



}

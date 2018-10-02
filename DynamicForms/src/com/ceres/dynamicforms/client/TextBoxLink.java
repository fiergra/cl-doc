package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.TextBoxBase;

public class TextBoxLink<T> extends InteractorWidgetLink<T> {

	private static int count;
	private IGetter<String, T> getter;
	private ISetter<String, T> setter;
	
	public TextBoxLink(Interactor<T> interactor, TextBoxBase txtBox, IGetter<String, T> getter, ISetter<String, T> setter, boolean required) {
		super(interactor, "TextBoxLink#" + (count++), txtBox, makeAttributes(required, false, false, false));
		this.getter = getter;
		this.setter = setter;
	}

	public static HashMap<String, String> makeAttributes(boolean required, boolean enabled, boolean focus, boolean isDefault) {
		HashMap<String, String> attributes = new HashMap<>(4);
		attributes.put(InteractorWidgetLink.REQUIRED, String.valueOf(required));
		attributes.put(InteractorWidgetLink.ENABLED, String.valueOf(enabled));
		attributes.put(InteractorWidgetLink.FOCUS, String.valueOf(focus));
		attributes.put(InteractorWidgetLink.DEFAULT, String.valueOf(isDefault));
		return attributes;
	}

	
	@Override
	public void toDialog(T item) {
		((TextBoxBase)getWidget()).setText(getter.get(item));
	}

	@Override
	public void fromDialog(T item) {
		setter.set(item, ((TextBoxBase)getWidget()).getText());
	}

	@Override
	public boolean isEmpty() {
		String text = ((TextBoxBase)getWidget()).getText();
		return text == null || text.length() == 0;
	}

}

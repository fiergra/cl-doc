package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.ITranslator;
import com.ceres.dynamicforms.client.Interactor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public abstract class ObjectSelectorBoxFactory<T extends Serializable> implements ILinkFactory<Map<String, Serializable>> {

	protected final ITranslator<Map<String, Serializable>> application;
	private static int count = 0;
	
	public ObjectSelectorBoxFactory(ITranslator<Map<String, Serializable>> application) {
		this.application = application;
	}

	protected abstract ObjectSelectorComboBox<T> createWidget(final HashMap<String, String> attributes);
	
	@Override
	public ObjectSelectorLink<T> createLink(final Interactor<Map<String, Serializable>> interactor, String name, final HashMap<String, String> attributes) {
		final ObjectSelectorComboBox<T> objectSelectorBox = createWidget(attributes);
		final ObjectSelectorLink<T> link = new ObjectSelectorLink<T>(interactor, name == null ? "ObjectSelectorBoxFactoryLink#" + count++ : name, objectSelectorBox, attributes);
		objectSelectorBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(link);
			}
		});
		return link;
	}
	

}

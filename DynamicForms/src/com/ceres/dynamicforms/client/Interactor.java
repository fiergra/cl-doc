package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Interactor {
	
	private final Collection<InteractorLink> links = new ArrayList<InteractorLink>();
	private Runnable changeHandler;
	private boolean isModified;
	private boolean isValid;
	
	public void addLink(InteractorLink link) {
		links.add(link);
	}

	public void toDialog(Map<String,Serializable> item) {
		for (InteractorLink il:links) {
			il.toDialog(item);
			if (il.isValid()) {
				il.getWidget().removeStyleName("invalidContent");
			} else {
				il.getWidget().addStyleName("invalidContent");
			}
		}
		isModified = false;
	}

	public void fromDialog(Map<String, Serializable> item) {
		for (InteractorLink il:links) {
			il.fromDialog(item);
		}
	}

	public boolean isModified() {
		return isModified;
	}

	public void setChangeHandler(Runnable changeHandler) {
		this.changeHandler = changeHandler;
	}

	public boolean isValid() {
		return isValid;
	}

	public void onChange(InteractorLink link) {
		isModified = true;
		if (!link.isValid()) {
			isValid = false;
			link.getWidget().addStyleName("invalidContent");
		} else {
			link.getWidget().removeStyleName("invalidContent");
			if (!isValid){
				validateAll();
			}
		}
		changeHandler.run();
	}

	public boolean isEmpty() {
		boolean isEmpty = true;
		Iterator<InteractorLink> iter = links.iterator();
		while (isEmpty && iter.hasNext()) {
			isEmpty = isEmpty && iter.next().isEmpty();
		}
		return isEmpty;
	}

	private void validateAll() {
		isValid = true;
		Iterator<InteractorLink> iter = links.iterator();
		while (isValid && iter.hasNext()) {
			isValid = isValid && iter.next().isValid();
		}
	}

}

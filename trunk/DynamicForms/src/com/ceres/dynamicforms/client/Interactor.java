package com.ceres.dynamicforms.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Interactor {
	
	private final Collection<InteractorLink> links = new ArrayList<InteractorLink>();
	private Runnable changeHandler;
	private boolean isModified;
	
	public void addLink(InteractorLink link) {
		links.add(link);
	}

	public void toDialog(INamedValues item) {
		for (InteractorLink il:links) {
			il.toDialog(item);
		}
		isModified = false;
	}

	public void fromDialog(INamedValues item) {
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
		return true;
	}

	public void onChange(InteractorLink textLink) {
		isModified = true;
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

}

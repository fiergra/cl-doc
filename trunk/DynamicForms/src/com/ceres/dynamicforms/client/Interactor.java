package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Interactor {
	
	private final Collection<InteractorLink> links = new ArrayList<InteractorLink>();
	private List<Runnable> changeHandlers;
	private boolean isModified;
	private boolean isValid;
	
	public void addLink(InteractorLink link) {
		links.add(link);
	}

	public void resetLinks() {
		links.clear();
	}
	
	public void toDialog(Map<String,Serializable> item) {
		for (InteractorLink il:links) {
			il.toDialog(item);
			il.hilite(il.isValid());
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

	public void addChangeHandler(Runnable changeHandler) {
		if (changeHandlers == null) {
			changeHandlers = new ArrayList<Runnable>();
		}
		changeHandlers.add(changeHandler);
	}

	public boolean isValid() {
		return isValid;
	}

	
	public void onChange(InteractorLink link) {
		isModified = true;
		link.hilite(link.isValid());
		if (!link.isValid()) {
			isValid = false;
		} else {
			if (!isValid){
				validateAll();
			}
		}

		
		if (changeHandlers != null) {
			for (Runnable changeHandler:changeHandlers) {
				changeHandler.run();
			}
		}
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

	public void hilite(boolean valid) {
		
	}

}
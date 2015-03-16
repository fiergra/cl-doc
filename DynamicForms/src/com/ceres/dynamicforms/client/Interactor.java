package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class Interactor {
	
	private final Collection<InteractorLink> links = new HashSet<InteractorLink>();
	private List<LinkChangeHandler> changeHandlers;
	private boolean isModified;
	private Boolean isValid = null;
	
	public void addLink(InteractorLink link) {
		links.add(link);
	}

	public InteractorLink getLink(String name) {
		InteractorLink result = null;
		Iterator <InteractorLink> iter = links.iterator(); 
		while (result == null && iter.hasNext()) {
			InteractorLink curr = iter.next();
			if (name.equals(curr.getName())) {
				result = curr;
			}
		}
		return result;
	}
	
	public void resetLinks() {
		links.clear();
	}
	
	public void toDialog(Map<String,Serializable> item) {
		if (changeHandlers != null) {
			for (LinkChangeHandler changeHandler:changeHandlers) {
				changeHandler.toDialog(item);
			}
		}
		for (InteractorLink il:links) {
			il.toDialog(item);
			il.hilite(il.isValid());
		}
		isModified = false;
	}

	public void fromDialog(Map<String, Serializable> item) {
		if (changeHandlers != null) {
			for (LinkChangeHandler changeHandler:changeHandlers) {
				changeHandler.fromDialog(item);
			}
		}
		for (InteractorLink il:links) {
			il.fromDialog(item);
		}
	}

	public boolean isModified() {
		return isModified;
	}

	public static abstract class LinkChangeHandler {
		protected void toDialog(Map<String,Serializable> item) {}
		protected void fromDialog(Map<String,Serializable> item) {}
		protected abstract void onChange(InteractorLink link);
	}
	
	public void addChangeHandler(LinkChangeHandler changeHandler) {
		if (changeHandlers == null) {
			changeHandlers = new ArrayList<LinkChangeHandler>();
		}
		changeHandlers.add(changeHandler);
	}

	public boolean isValid() {
		if (isValid == null) {
			validateAll();
		}
		return isValid;
	}

	
	public void onChange(InteractorLink link) {
		isModified = true;
		link.hilite(link.isValid());
		if (!link.isValid()) {
			isValid = false;
		} else {
			if (isValid == null || !isValid){
				validateAll();
			}
		}

		
		if (changeHandlers != null) {
			for (LinkChangeHandler changeHandler:changeHandlers) {
				changeHandler.onChange(link);
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

	public void enable(boolean enabled) {
		for (InteractorLink il:links) {
			il.enable(enabled);
		}
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public void setFocus() {
		Focusable f = null;
		Iterator<InteractorLink>linkIter = links.iterator();
		
		while (f == null && linkIter.hasNext()) {
			InteractorLink curr = linkIter.next();
			if (curr instanceof InteractorWidgetLink && ((InteractorWidgetLink)curr).getWidget() instanceof Focusable) {
				f = (Focusable) ((InteractorWidgetLink)curr).getWidget();
			}
		}
		
		if (f != null) {
			f.setFocus(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String linkName) {
		InteractorLink link = getLink(linkName);
		InteractorWidgetLink widgetLink = (InteractorWidgetLink) link;
		return (T) widgetLink.getWidget();
	}


}

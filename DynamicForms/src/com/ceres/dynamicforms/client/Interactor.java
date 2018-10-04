package com.ceres.dynamicforms.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class Interactor<T> {
	
	private final Collection<InteractorLink<T>> links = new HashSet<InteractorLink<T>>();
	private List<LinkChangeHandler<T>> changeHandlers;
	private boolean isModified;
	private Boolean isValid = null;
	private T displayedItem;
	
	public void addLink(InteractorLink<T> link) {
		links.add(link);
	}

	
	public T getDisplayedItem() {
		return displayedItem;
	}

	public InteractorLink<T> getLink(String name) {
		InteractorLink<T> result = null;
		Iterator <InteractorLink<T>> iter = links.iterator(); 
		while (result == null && iter.hasNext()) {
			InteractorLink<T> curr = iter.next();
			if (name.equals(curr.getName())) {
				result = curr;
			}
		}
		return result;
	}
	
	public void resetLinks() {
		links.clear();
	}
	
	public void resetChangeHandlers() {
		changeHandlers.clear();
	}
	
	public void toDialog(ITranslator<T> translator, T item) {
		
		this.displayedItem = item;
		
		if (translator == null) {
			translator = new SimpleTranslator<T>();
		}
		if (changeHandlers != null) {
			for (LinkChangeHandler<T> changeHandler:changeHandlers) {
				changeHandler.beforeToDialog(item);
			}
		}
		for (InteractorLink<T> il:links) {
			Widget widget;
			if (il instanceof InteractorWidgetLink) {
				widget = ((InteractorWidgetLink<T>) il).getWidget();
				String objectType = ((InteractorWidgetLink<T>) il).getObjectType();
				if (objectType != null) {
					boolean isVisible = translator.isVisible(item, objectType);
					boolean isEnabled = translator.isEnabled(item, objectType);
					widget.setVisible(isVisible);
					il.enable(isEnabled);
					((InteractorWidgetLink<T>) il).requestFocus();
				}
			}

			il.toDialog(item);
			boolean linkIsValid = il.isValid();
			il.hilite(linkIsValid);
			
			isValid = isValid == null ? linkIsValid : linkIsValid && isValid;
		}
		isModified = false;
		
		if (changeHandlers != null) {
			for (LinkChangeHandler<T> changeHandler:changeHandlers) {
				changeHandler.afterToDialog(item);
			}
		}

	}

	public void fromDialog(T item) {
		if (changeHandlers != null) {
			for (LinkChangeHandler<T> changeHandler:changeHandlers) {
				changeHandler.fromDialog(item);
			}
		}
		for (InteractorLink<T> il:links) {
			il.fromDialog(item);
		}
	}

	public boolean isModified() {
		return isModified;
	}

	public static abstract class LinkChangeHandler<T> {
		protected void afterToDialog(T item) {}
		public void beforeToDialog(T item) {}
		protected void fromDialog(T item) {}
		protected abstract void onChange(InteractorLink<T> link);
	}
	
	public void addChangeHandler(LinkChangeHandler<T> changeHandler) {
		if (changeHandlers == null) {
			changeHandlers = new ArrayList<LinkChangeHandler<T>>();
		}
		changeHandlers.add(changeHandler);
	}

	public boolean isValid() {
		if (isValid == null) {
			validateAll();
		}
		return isValid;
	}

	

	@SuppressWarnings("unchecked")
	public void revalidate() {
		boolean wasValid = isValid();
		validateAll();
		
		if (wasValid != isValid) {
			if (changeHandlers != null) {
				links.forEach(l -> l.hilite(l.isValid()));
			}
			if (changeHandlers != null) {
				changeHandlers.forEach(h -> h.onChange((InteractorLink<T>) InteractorLink.DUMMY));
			}
		}
	}

	public void onChange(InteractorLink<T> link) {
		isModified = true;
		if (link != null) {
			link.hilite(link.isValid());
			if (!link.isValid()) {
				isValid = false;
			} else {
				if (isValid == null || !isValid){
					validateAll();
				}
			}
		}
		if (changeHandlers != null) {
			for (LinkChangeHandler<T> changeHandler:changeHandlers) {
				changeHandler.onChange(link);
			}
		}
	}

	public boolean isEmpty() {
		boolean isEmpty = true;
		Iterator<InteractorLink<T>> iter = links.iterator();
		while (isEmpty && iter.hasNext()) {
			isEmpty = isEmpty && iter.next().isEmpty();
		}
		return isEmpty;
	}

	private void validateAll() {
		isValid = true;
		Iterator<InteractorLink<T>> iter = links.iterator();
		while (isValid && iter.hasNext()) {
			InteractorLink<T> curr = iter.next();
			GWT.log("checking link " + curr.name);
			isValid = isValid && curr.isValid();
			GWT.log(String.valueOf(isValid));
		}
	}

	public void hilite(boolean valid) {
		
	}

	public void enable(boolean enabled) {
		for (InteractorLink<T> il:links) {
			il.enable(enabled);
		}
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public void setFocus() {
		Focusable f = null;
		Iterator<InteractorLink<T>>linkIter = links.iterator();
		
		while (f == null && linkIter.hasNext()) {
			InteractorLink<T> curr = linkIter.next();
			if (curr instanceof InteractorWidgetLink && ((InteractorWidgetLink<T>)curr).getWidget() instanceof Focusable) {
				f = (Focusable) ((InteractorWidgetLink<T>)curr).getWidget();
			}
		}
		
		if (f != null) {
			f.setFocus(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <V extends Widget> V getWidget(String linkName) {
		InteractorLink<T> link = getLink(linkName);
		InteractorWidgetLink<T> widgetLink = (InteractorWidgetLink<T>) link;
		return (V) (widgetLink != null ? widgetLink.getWidget() : null);
	}


}

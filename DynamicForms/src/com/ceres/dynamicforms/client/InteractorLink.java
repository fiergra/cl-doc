package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

public abstract class InteractorLink {

	public static final InteractorLink DUMMY = new InteractorLink(null, "dummy") {
		
		@Override
		public void toDialog(Map<String, Serializable> item) {
		}
		
		@Override
		public boolean isEmpty() {
			return false;
		}
		
		@Override
		protected void hilite(boolean isValid) {
		}
		
		@Override
		public void fromDialog(Map<String, Serializable> item) {
		}
		
		@Override
		public void enable(boolean enabled) {
		}
	};
	
	protected final String name;
	protected final Interactor interactor;
	protected static Logger logger = Logger.getLogger("InteractorLink");

	public InteractorLink(Interactor interactor, String name) {
		this.interactor = interactor;
		this.name = name;
	}

	public abstract void toDialog(Map<String, Serializable> item);
	public abstract void fromDialog(Map<String, Serializable> item);
	public abstract void enable(boolean enabled);
	protected abstract void hilite(boolean isValid);
	public abstract boolean isEmpty();

	protected Serializable get(Map<String, Serializable> item, String fieldName) {
		Serializable value;
		int index = fieldName.indexOf('.');
		if (index == -1) {
			value = item.get(fieldName);
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Serializable> subItem = (Map<String, Serializable>)item.get(fieldName.substring(0, index));
			value = get(subItem, fieldName.substring(index + 1));
		}
		
		return value;
	}

	protected void put(Map<String, Serializable> item, String fieldName, Serializable value) {
		int index = fieldName.indexOf('.');
		if (index == -1) {
			item.put(fieldName, value);
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Serializable> subItem = (Map<String, Serializable>)item.get(fieldName.substring(0, index));
			put(subItem, fieldName.substring(index + 1), value);
		}
	}

	public boolean isValid() {
		return true;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (name != null && obj instanceof InteractorLink) {
			return name.equals(((InteractorLink)obj).name);
		}
		return super.equals(obj);
	}

	
}

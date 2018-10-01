package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

public abstract class InteractorLink<T> {

	public static final InteractorLink<Object> DUMMY = new InteractorLink<Object>(null, "dummy") {
		
		@Override
		public void toDialog(Object item) {
		}
		
		@Override
		public boolean isEmpty() {
			return false;
		}
		
		@Override
		protected void hilite(boolean isValid) {
		}
		
		@Override
		public void fromDialog(Object item) {
		}
		
		@Override
		public void enable(boolean enabled) {
		}
	};
	
	protected final String name;
	protected final Interactor<T> interactor;
	protected static Logger logger = Logger.getLogger("InteractorLink");

	public InteractorLink(Interactor<T> interactor, String name) {
		this.interactor = interactor;
		this.name = name;
	}

	public abstract void toDialog(T item);
	public abstract void fromDialog(T item);
	
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		InteractorLink other = (InteractorLink) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	
}

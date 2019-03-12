package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;

public class ObjectSelectorComboBox<T> extends ListBox {
	protected final List<T> entities = new ArrayList<T>();
	private T selectedEntity = null;
	private boolean nullsAllowed = true;
	private Comparator<T> comparator;
	private LabelFunc<T> lf;
	
	public static Logger log = Logger.getLogger(ObjectSelectorComboBox.class.getName());

	public ObjectSelectorComboBox(boolean nullsAllowed) {
		this(nullsAllowed, null);
	}

	public ObjectSelectorComboBox(boolean nullsAllowed, LabelFunc<T> labelFunc) {
		this.nullsAllowed = nullsAllowed;
		this.lf = labelFunc;
		setVisibleItemCount(1);
		comparator = new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				String s1 = labelFunc(o1);
				String s2 = labelFunc(o2);
				return s1 != null ? (s2 != null ? s1.compareTo(s2) : 0) : 0;
			}
		};
		addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				log.info(event.getNativeKeyCode() + " ");
				if (event.getNativeKeyCode() == 46) {
					setValue(null);
					setSelectedIndex(0);
					NativeEvent ne = Document.get ().createChangeEvent ();
			        ChangeEvent.fireNativeEvent (ne, ObjectSelectorComboBox.this);
				}
			}
		});
		
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int index = getSelectedIndex();
				if (index != -1) {
					selectedEntity = entities.get(index);
				} else {
					selectedEntity = null;
				}

			}
		});
	}

	
	
	@Override
	public void clear() {
		super.clear();
		entities.clear();
		selectedEntity = null;
	}

	public void populate(Collection<T> result) {
		
		int index = 0;
		
		clear();
		entities.clear();
		entities.addAll(result);
		
		Collections.sort(entities, comparator);

		if (nullsAllowed) {
			entities.add(0, null);
		}
		
		for (T e:entities) {
			addItem(labelFunc(e));
		}

		if (selectedEntity != null && entities != null) {
			index = entities.indexOf(selectedEntity);
			setSelectedIndex(index);
			if (index != -1) {
				selectedEntity = entities.get(index);
			}
		}
		
	}

	public void setNullsAllowed(boolean allowed) {
		nullsAllowed = allowed;
	}
	
	protected String labelFunc(T entity) {
		return lf != null ? lf.label(entity) : entity != null ? entity.toString() : "<null>";
	}
	
	public void setValue(T value) {
		selectedEntity = value;
		int index = entities.indexOf(value);
		
		if (index != -1) {
			selectedEntity = entities.get(index);
			setSelectedIndex(index);
		} else {
			entities.add(value);
			addItem(labelFunc(value));

			setValue(value);
//			if (nullsAllowed){
//				setSelectedIndex(0);
//			}
		}
	}
	
	public T getValue() {
		return selectedEntity;
	}
	
	public List<T>getSelectedItems() {
		List<T> selection = new ArrayList<>();
		
		if (entities != null && getSelectedIndex() > -1) {
			for (int index = getSelectedIndex(); index < entities.size(); index++) {
				if (isItemSelected(index)) {
					selection.add(entities.get(index));
				}
			}
		}
		
		return selection;
	}

	/**
	 * @param comparator
	 * Set a different comparator in case ordering alphabetically is not desired
	 */
	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	public void removeItem(T value) {
		int index = entities.indexOf(value);
		if (index != -1) {
			removeItem(index);
			entities.remove(index);
			setValue(null);
			setSelectedIndex(0);
		}
	}
	
}

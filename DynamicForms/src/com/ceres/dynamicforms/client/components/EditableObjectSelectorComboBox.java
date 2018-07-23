package com.ceres.dynamicforms.client.components;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class EditableObjectSelectorComboBox<T> extends HorizontalPanel {
	
	private final ObjectSelectorComboBox<T> cmb;
	private final PushButton pbAdd = new PushButton(new Image("assets/images/add16.png"));
	private final PushButton pbDelete = new PushButton(new Image("assets/images/delete-icon.16.png"));
	
	public EditableObjectSelectorComboBox(boolean nullsAllowed, Runnable onAdd, Runnable onDelete) {
		this(nullsAllowed, null, onAdd, onDelete);
	}

	public EditableObjectSelectorComboBox(boolean nullsAllowed, LabelFunc<T> labelFunc, Runnable onAdd, Runnable onDelete) {
		cmb = new ObjectSelectorComboBox<>(nullsAllowed, labelFunc);
		add(cmb);

		pbAdd.setPixelSize(16, 16);
		add(pbAdd);
		pbAdd.addClickHandler(e -> onAdd.run());

		if (onDelete != null) {
			pbDelete.setPixelSize(16, 16);
			pbDelete.setEnabled(false);
			pbDelete.addClickHandler(e -> onDelete.run());
			add(pbDelete);
			cmb.addChangeHandler(e -> pbDelete.setEnabled(cmb.getValue() != null));
		}
		
	}

	public void populate(List<T> list) {
		cmb.populate(list);
	}

	public void addChangeHandler(ChangeHandler handler) {
		cmb.addChangeHandler(handler);
	}

	public void setValue(T value) {
		cmb.setValue(value);
	}

	public T getValue() {
		return cmb.getValue();
	}

	public void removeItem(T value) {
		cmb.removeItem(value);
		pbDelete.setEnabled(cmb.getValue() != null);
	}

}

package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class OnDemandComboBox <T> extends DockLayoutPanel implements IEntitySelector<T> {

	private ListRetrievalService<T> listRetrievalService;
	private LabelFunction<T> labelFunction;
	private final TextBox txtFilter = new TextBox();
	private final ToggleButton pbOpen = new ToggleButton("...");
	private List<T> itemList;
	private ClDoc clDoc;
	private OnDemandChangeListener<T> changeListener;
	
	public OnDemandComboBox(ClDoc clDoc, ListRetrievalService<T> listRetrievalService, LabelFunction <T> labelFunxtion, OnDemandChangeListener<T>changeListener) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setHeight("2em");
		this.listRetrievalService = listRetrievalService;
		this.labelFunction = labelFunxtion;
		this.changeListener = changeListener;
		
		pbOpen.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (pbOpen.isDown()) {
					showPopup();
				} else {
					hidePopup();
				}
			}
		});
		pbOpen.setWidth("2em");
		addEast(pbOpen, 2);
		add(txtFilter);
		txtFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.isDownArrow() && pp != null) {
					listBox.setFocus(true);
				} else if (event.getNativeKeyCode() != KeyCodes.KEY_TAB) {
					retrieve(txtFilter.getValue());
				}
			}
		});
	}

	protected void retrieve(String value) {
		listRetrievalService.retrieve(value, new DefaultCallback<List<T>>(clDoc, "retrieve") {

			@Override
			public void onSuccess(List<T> result) {
				itemList = result;
				createOrUpdatePopup(result);
			}
		});
	}

	private void showPopup() {
		retrieve(txtFilter.getValue());
	}

	private void hidePopup() {
		if (pp != null) {
			pp.hide();
			pp = null;
			pbOpen.setDown(false);
			txtFilter.setFocus(true);
		}
	}
	
	protected String labelFunction(T item) {
		return labelFunction.getLabel(item);
	}
	
	protected String valueFunction(T item) {
		return labelFunction.getValue(item);
	}

	private PopupPanel pp = null;
	private ListBox listBox = null;
	private T selectedItem;

	private void createOrUpdatePopup(List<T> list) {
		if (pp == null) {
			pp = new PopupPanel(false);
			listBox = new ListBox();
			listBox.setSize((txtFilter.getOffsetWidth() * 2) + "px", "150px");
			listBox.setVisibleItemCount(Integer.MAX_VALUE);
			listBox.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					setSelectedIndex(listBox.getSelectedIndex());
					hidePopup();
				}
			});
			listBox.addKeyUpHandler(new KeyUpHandler() {
				
				@Override
				public void onKeyUp(KeyUpEvent event) {
					if (event.isUpArrow() && listBox.getSelectedIndex() == 0) {
						txtFilter.setFocus(true);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
						hidePopup();
						txtFilter.setFocus(true);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						setSelectedIndex(listBox.getSelectedIndex());
						hidePopup();
					}
				}
			});
			listBox.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					hidePopup();
				}
			});
			
			pp.setWidget(listBox);
			pp.setPopupPosition(txtFilter.getAbsoluteLeft(), txtFilter.getAbsoluteTop() + txtFilter.getOffsetHeight());
			pp.show();
		} else {
			listBox.clear();
		}
		
		for (T item:list) {
			listBox.addItem(labelFunction(item), valueFunction(item));
		}
	}

	private void setSelectedItem(T item) {
		T oldValue = selectedItem;
		selectedItem = item;
		changeListener.onChange(oldValue, selectedItem);
	}
	
	private void setSelectedIndex(int selectedIndex) {
		setSelectedItem(itemList.get(selectedIndex));
		txtFilter.setValue(labelFunction(selectedItem));
	}

	public boolean setSelected(T item) {
		if ((item == null && selectedItem != null) || (item != null && !item.equals(selectedItem))) {
			selectedItem = item;
			txtFilter.setValue(labelFunction(item));
		}
		return itemList != null && itemList.contains(selectedItem);
	}
	
	public T getSelected() {
		return selectedItem;
	}

}

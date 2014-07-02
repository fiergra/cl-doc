package com.ceres.cldoc.client.controls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.ceres.cldoc.client.views.OnClick;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class OnDemandComboBox <T extends Serializable> extends HorizontalPanel implements IEntitySelector<T> {

	private final ListRetrievalService<T> listRetrievalService;
	private final LabelFunction<T> labelFunction;
	private final TextBox txtFilter = new TextBox();
	
//	private final ToggleButton pbOpen = new ToggleButton("...");
//	private final Image pbNew = new Image("icons/16/Button-Add-01.png");
	
	private List<T> itemList;
	private final ClDoc clDoc;
//	private final OnDemandChangeListener<T> changeListener;
	
	public OnDemandComboBox(final ClDoc clDoc, 
			ListRetrievalService<T> listRetrievalService, 
			LabelFunction <T> labelFunxtion, 
			final OnClick<TextBox> notFound,
			final Runnable onClick) {
		super();
		this.clDoc = clDoc;
//		setHeight("2em");
		setVerticalAlignment(ALIGN_MIDDLE);
		this.listRetrievalService = listRetrievalService;
		this.labelFunction = labelFunxtion;
//		this.changeListener = changeListener;
		
////		pbOpen.addClickHandler(new ClickHandler() {
////			
////			@Override
////			public void onClick(ClickEvent event) {
////				if (pbOpen.isDown()) {
////					showPopup();
////				} else {
////					hidePopup();
////				}
////			}
////		});
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		hp.add(pbOpen);
//		
//		if (onClick != null) {
//			hp.add(pbNew);
//			
//			pbNew.addClickHandler(new ClickHandler() {
//				
//				@Override
//				public void onClick(ClickEvent event) {
//					onClick.run();
//				}
//			});
//		}
		txtFilter.setWidth("100%");
		add(txtFilter);
//		add(hp);
		txtFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.isDownArrow() && dropDownPopup != null) {
					listBox.setFocus(true);
					listBox.setSelectedIndex(0);
				} else if (event.getNativeKeyCode() != KeyCodes.KEY_TAB) {
					setSelectedItem(null);
					retrieve(txtFilter.getValue());
				}
			}
		});
		txtFilter.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (notFound != null && selectedItem == null && txtFilter.getText().length() > 0 && dropDownPopup == null) {
					notFound.onClick(txtFilter);
				}
			}
		});
//		txtFilter.addFocusHandler(new FocusHandler() {
//			
//			@Override
//			public void onFocus(FocusEvent event) {
//				System.out.println(event);	
//			}
//		});
	}

	public void refresh() {
		retrieve(getText());
	}
	
	protected void retrieve(String value) {
		listRetrievalService.retrieve(value, new DefaultCallback<List<T>>(clDoc, "retrieve") {

			@Override
			public void onResult(List<T> result) {
				itemList = result;
				if (itemList.isEmpty()) {
					hidePopup();
				} else {
					createOrUpdatePopup(result);
				}
			}
		});
	}

//	private void showPopup() {
//		retrieve(txtFilter.getValue());
//	}

	private void hidePopup() {
		if (dropDownPopup != null) {
			dropDownPopup.hide();
			dropDownPopup = null;
//			pbOpen.setDown(false);
			txtFilter.setFocus(true);
		}
	}
	
	protected String labelFunction(T entity) {
		return entity != null ? labelFunction.getLabel(entity) : "";
	}
	
	protected String valueFunction(T entity) {
		return entity != null ? labelFunction.getValue(entity) : null;
	}

	private PopupPanel dropDownPopup = null;
	private ListBox listBox = null;
	private T selectedItem;

	private void createOrUpdatePopup(List<T> list) {
		if (dropDownPopup == null) {
			dropDownPopup = new PopupPanel(false);
			dropDownPopup.addStyleName("higherThanHigh");
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
			
			dropDownPopup.setWidget(listBox);
			dropDownPopup.setPopupPosition(txtFilter.getAbsoluteLeft(), txtFilter.getAbsoluteTop() + txtFilter.getOffsetHeight());
			dropDownPopup.show();
		} else {
			listBox.clear();
		}
		
		for (T act:list) {
			listBox.addItem(labelFunction(act), valueFunction(act));
		}
	}

	private void setSelectedItem(T item) {
		T oldValue = selectedItem;
		selectedItem = item;
//		changeListener.onChange(oldValue, selectedItem);

		for (ChangeHandler ch:changeHandlers) {
			ch.onChange(null);
		}

	}
	
	private void setSelectedIndex(int selectedIndex) {
		setSelectedItem(itemList.get(selectedIndex));
		txtFilter.setValue(labelFunction(selectedItem));
	}

	@Override
	public boolean setSelected(T item) {
		if ((item == null && selectedItem != null) || (item != null && !item.equals(selectedItem))) {
			selectedItem = item;
			txtFilter.setValue(labelFunction(item));
		}
		return itemList != null && itemList.contains(selectedItem);
	}
	
	@Override
	public T getSelected() {
		return selectedItem;
	}

	public String getText() {
		return txtFilter.getText();
	}

	private final List<ChangeHandler> changeHandlers = new ArrayList<ChangeHandler>();

	@Override
	public void addSelectionChangedHandler(ChangeHandler changeHandler) {
		changeHandlers.add(changeHandler);
	}

	public void setInnerWidth(String string) {
		txtFilter.setWidth(string);
	}

	public void setInnerStyleName(String style) {
		txtFilter.setStyleName(style);
	}

	@Override
	public void addStyleName(String style) {
		txtFilter.addStyleName(style);
	}

	@Override
	public void removeStyleName(String style) {
		txtFilter.removeStyleName(style);
	}

	

}

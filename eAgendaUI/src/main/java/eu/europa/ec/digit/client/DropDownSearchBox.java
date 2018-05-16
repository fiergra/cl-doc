package eu.europa.ec.digit.client;

import java.util.List;

import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.components.EnabledHorizontalPanel;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.ObjectSelectorComboBox;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchBox;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class DropDownSearchBox <T> extends EnabledHorizontalPanel {
	
	private SearchBox<T> searchBox;
	private PushButton pbDropDown = new PushButton(new Image("drop-down-arrow.png"));
	private ObjectSelectorComboBox<T> lbDropDown;
	
	public DropDownSearchBox(RunSearch<T> runSearch, LabelFunc<T> lf) {
		HorizontalPanel hpImage = new HorizontalPanel();
		hpImage.setSpacing(3);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		searchBox = new RemoteSearchBox<>(new SimpleTranslator(), runSearch, lf, lf);
		add(searchBox);

		lbDropDown = new ObjectSelectorComboBox<T>(false, lf);
		PopupPanel popUp = new PopupPanel();
		popUp.add(lbDropDown);
		lbDropDown.setHeight("6em");
		pbDropDown.setStyleName("flatButton");
		pbDropDown.addClickHandler(e->togglePopup(popUp));
		pbDropDown.setEnabled(false);
		lbDropDown.addChangeHandler(e-> { onDropDownSelection(popUp);});
		lbDropDown.addClickHandler(e-> { onDropDownSelection(popUp);});
		lbDropDown.addBlurHandler(e->togglePopup(popUp));
		lbDropDown.setVisibleItemCount(5);
		hpImage.add(pbDropDown);
		add(hpImage);
	}
	
	private void onDropDownSelection(PopupPanel popUp) {
		searchBox.setSelected(lbDropDown.getValue()); 
		togglePopup(popUp);
//		MultiWordSuggestion suggestion = new MultiWordSuggestion();
//		SelectionEvent.fire(searchBox, suggestion); 
		
	}
	
	public void populate(List<T> items) {
		lbDropDown.populate(items);
		pbDropDown.setEnabled(!items.isEmpty());
	}
	
	public T getSelected() {
		return searchBox.getSelected();
	}

	public void addDropDownChangeHandler(ChangeHandler handler) {
		lbDropDown.addChangeHandler(handler);
	}
	
	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return searchBox.addSelectionHandler(handler);
	}

	@Override
	public void setEnabled(boolean enabled) {
		searchBox.setEnabled(enabled);
		pbDropDown.setEnabled(lbDropDown.getItemCount() > 0);
	}

	private void togglePopup(PopupPanel popUp) {
		if (popUp.isShowing()) {
			popUp.hide();
			searchBox.setFocus(true);
		} else if (isEnabled()){
			popUp.setWidth(getOffsetWidth() + "px");
			popUp.showRelativeTo(this);
			lbDropDown.setFocus(true);
		}
	}

	public void setSelected(T value) {
		searchBox.setSelected(value);
	}
	
}

package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditableComboBox<T> extends FlexTable {
	
	public interface ItemFormatter<T> {
		String format(T item);
	}

	public interface ItemChangeHandler<T> {
		void beforeChange(T oldSelection, T newSelection);
	}

	private final TextBox textBox = new TextBox();
	private final PushButton pbDropDown = new PushButton(new Image("assets/images/24x24/drop-down-arrow.white.png"));
	
	private List<T> items = new ArrayList<>();
	private T selectedItem = null;
	
	private ItemFormatter<T> formatter;
	private ItemChangeHandler<T> changeHandler;

	public EditableComboBox() {
		textBox.setWidth("100%");
		setWidget(0, 0, textBox);
		getFlexCellFormatter().setWidth(0, 0, "100%");
		pbDropDown.setStyleName("flatButton");
		setWidget(0, 1, pbDropDown);
		pbDropDown.addClickHandler(e -> toggleDropDown());
	}
	
	
	private PopupPanel popUp = new PopupPanel(true, true);
	
	private void toggleDropDown() {
		if (popUp.isShowing()) {
			popUp.hide();
		} else {
			popUp.clear();
			VerticalPanel vpPopup = new VerticalPanel();
			vpPopup.setWidth(textBox.getOffsetWidth() + "px");
			items.forEach(i -> {
				String s = formatter != null ? formatter.format(i) : String.valueOf(i);
				Label l = new Label(s);
				l.setStyleName("editableComboBoxPopupItem");
				vpPopup.add(l);
				l.addClickHandler(e -> {
					setSelectedItem(i, true);
					popUp.hide();
				});
			});
			popUp.add(vpPopup);
			popUp.showRelativeTo(textBox);
		}
	}

	private void setSelectedItem(T i, boolean b) {
		if (b) {
			notifyChangeHandler(i);
		}
		selectedItem = i;
		textBox.setText(formatter != null ? formatter.format(i) : String.valueOf(i));
	}

	private void notifyChangeHandler(T i) {
		if (changeHandler != null) {
			changeHandler.beforeChange(selectedItem, i);
		}
	}

	public TextBox getTextBox() {
		return textBox;
	}

	public void addItem(T item) {
		items.add(item);
	}
	
	public void setFormatter(ItemFormatter<T> formatter) {
		this.formatter = formatter;
	}
	
	public void setChangeHandler(ItemChangeHandler<T> changeHandler) {
		this.changeHandler = changeHandler;
	}
	
	
}

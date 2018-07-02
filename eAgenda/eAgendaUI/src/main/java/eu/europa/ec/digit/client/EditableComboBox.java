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
		void onChange(T newSelection);
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

	public void setSelectedItem(T i, boolean b) {
		selectedItem = i;
		if (b) {
			notifyChangeHandler(i);
		}
		if (i != null) {
			textBox.setText(formatter != null ? formatter.format(i) : String.valueOf(i));
		} else {
			textBox.setText(null);
		}
	}

	private void notifyChangeHandler(T i) {
		if (changeHandler != null) {
			changeHandler.onChange(i);
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
	
	public T getSelectedItem() {
		return selectedItem;
	}

	public void removeItem(T item) {
		items.remove(item);
		if (selectedItem == item) {
			if (items.isEmpty()) {
				setSelectedItem(null, true);
			} else {
				setSelectedItem(items.get(0), true);
			}
		}
		
	}

	public void setSelectedItem(int i, boolean b) {
		T item = items.get(i);
		setSelectedItem(item, b);
	}
	
}

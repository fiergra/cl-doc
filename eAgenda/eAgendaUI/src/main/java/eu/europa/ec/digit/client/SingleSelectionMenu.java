package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

public class SingleSelectionMenu {
	
	private List<MenuItem> menuItems = new ArrayList<>();
	private MenuItem selectedMenuItem = null;
	
	public MenuItem addItem(Panel vpMenuItems, Image image, String label, Runnable onClick) {
		MenuItem mItem = new MenuItem(image, label, onClick);
		menuItems.add(mItem);
		mItem.addClickHandler(e -> {
			selectItem(mItem);
		});
		vpMenuItems.add(mItem);
		return mItem;
	}

	public void selectItem(int index) {
		MenuItem mItem = menuItems.get(index);
		selectItem(mItem);
	}
	
	public void selectItem(MenuItem mItem) {
		if (selectedMenuItem != null) {
			selectedMenuItem.removeStyleDependentName("selected");
		}
		selectedMenuItem = mItem;
		selectedMenuItem.addStyleDependentName("selected");
		mItem.onClick();
	}

	public void removeItem(Panel vpMenuItems, String displayName) {
		menuItems.stream().filter(m -> m.getText().equals(displayName)).forEach(mi -> {
			vpMenuItems.remove(mi);
			menuItems.remove(mi);
		});
		
		if (!menuItems.isEmpty()) {
			selectItem(0);
		}
	}
	
}

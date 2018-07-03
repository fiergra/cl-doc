package eu.europa.ec.digit.client;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class MenuItem extends FocusPanel {

	public final Runnable onClick;
	public final Label label;
	public final HorizontalPanel hpRight = new HorizontalPanel();
	
	public MenuItem(Image image, String string, Runnable onClick) {
		setWidth("100%");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSize("100%", "100%");
		hp.setStyleName("menuItemContent");

		HorizontalPanel hpLeft = new HorizontalPanel();
		hpLeft.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpLeft.setSpacing(5);
		hpLeft.setStyleName("menuItemLeft");

		label = new Label(string);
		hpLeft.add(image);
		hpLeft.add(label);

		hpRight.setWidth("100%");
		hpRight.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hpRight.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpRight.setStyleName("menuItemRight");

		hp.add(hpLeft);
		hp.add(hpRight);
		
		add(hp);
		setStyleName("menuItem");
		this.onClick = onClick;
	}

	public void onClick() {
		onClick.run();
	}

	public String getText() {
		return label.getText();
	}

}

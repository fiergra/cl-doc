package eu.europa.ec.digit.client;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class MenuItem extends FocusPanel {

	public final Runnable onClick;
	public final Label label;

	public MenuItem(Image image, String string, Runnable onClick) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSpacing(5);
		label = new Label(string);
		hp.add(image);
		hp.add(label);
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

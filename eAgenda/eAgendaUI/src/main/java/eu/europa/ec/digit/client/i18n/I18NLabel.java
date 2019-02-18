package eu.europa.ec.digit.client.i18n;

import com.google.gwt.user.client.ui.Label;

public class I18NLabel extends Label {

	public I18NLabel(String s) {
		super(StringResources.getLabel(s));
		
		String title = StringResources.getLabel(StringResources.TITLE + s);
		if (title != null) {
			setTitle(title);
		}
		
		if (StringResources.canEdit()) {
			addClickHandler(e -> { 
				if (e.isControlKeyDown()) {
					e.stopPropagation();
					StringResources.edit(s);
				}
			});
		}
	}

	public I18NLabel() {
	}

	@Override
	public void setText(String text) {
		super.setText(StringResources.getLabel(text));
		
		if (getTitle() == null || getTitle().length() == 0) {
			setTitle("here goes the online help for '" + text + "'");
		}
	}
	
	

}

/*
package eu.europa.ec.digit.client.i18n;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class I18NLabel extends HorizontalPanel {
	
	private Label label;

	public I18NLabel(String s) {
		label = new Label(StringResources.getLabel(s));
		add(label);
		if (StringResources.canEdit()) {
			label.addClickHandler(e -> { 
				if (e.isControlKeyDown()) {
					e.stopPropagation();
					StringResources.edit(s);
				}
			});
		}
	}

	public I18NLabel() {
		label = new Label();
	}

	public void setText(String text) {
		label.setText(StringResources.getLabel(text));
	}

	public void addStyleName(String styleName) {
		label.addStyleName(styleName);
	}
}
*/
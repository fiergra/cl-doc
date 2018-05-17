package eu.europa.ec.digit.client.i18n;

import com.google.gwt.user.client.ui.Label;

public class I18NLabel extends Label {

	public I18NLabel(String s) {
		super(StringResources.getLabel(s));
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
	}
	
	

}

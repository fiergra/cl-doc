package eu.europa.ec.digit.client.i18n;

import com.google.gwt.user.client.ui.Label;

public class I18NLabel2 extends Label {

	public I18NLabel2(String s) {
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

	public I18NLabel2() {
	}

	@Override
	public void setText(String text) {
		super.setText(StringResources.getLabel(text));
	}
	
	

}

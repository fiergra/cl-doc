package eu.europa.ec.digit.client;

import com.google.gwt.user.client.ui.HTML;

public class FAIcon extends HTML {

	public FAIcon(String name, int size) {
		this.setHTML("<i class=\"far fa-" + name + " fa-" + size + "x\"/>");
	}
	
}

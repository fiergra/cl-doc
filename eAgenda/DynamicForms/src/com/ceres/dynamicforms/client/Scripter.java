package com.ceres.dynamicforms.client;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.ui.HTML;

public class Scripter extends HTML {
	private String script;
	public Scripter(String scriptBody) {
		this.script = scriptBody;
		if (script.startsWith("<Script>")) {
			script = script.substring("<Script>".length(), script.length() - "</Script>".length());
			if (script.startsWith("<![CDATA[")) {
				script = script.substring("<![CDATA[".length(), script.length() - "]]>".length());
			}
		}
		
		addAttachHandler((e) -> {if (e.isAttached()) { ScriptInjector.fromString(script).setWindow(ScriptInjector.TOP_WINDOW).inject(); }});
	}
}

package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class LayoutEditor extends DockLayoutPanel {

	private final LayoutDefinition layoutDefinition;
	private final TextArea txtArea = new TextArea();

	public LayoutEditor(ClDoc clDoc, Act act, LayoutDefinition layoutDefinition, final OnClick<String> onParseOk) {
		super(Unit.EM);
		this.layoutDefinition = layoutDefinition;
		txtArea.setText(layoutDefinition.xmlLayout);
		add(txtArea);
		
		txtArea.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				try {
					String xml = txtArea.getText();
					Document document = XMLParser.parse(xml);
					onParseOk.onClick(xml);
				} catch (Exception x) {
					
				}
				
			}
		});
	}

	public void fromDialog() {
		layoutDefinition.xmlLayout = txtArea.getText();
	}

}

package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.ceres.cldoc.shared.layout.FormDesc;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public class Styler extends SplitLayoutPanel {
	private ClDoc clDoc;

	public Styler(ClDoc clDoc) {
		this.clDoc = clDoc;
		setup();
	}


	private void setup() {
		final TextArea layoutDesc = new TextArea();
		ValueBag dummy = new ValueBag("dummy");
		final Form <ValueBag> form = new Form<ValueBag>(dummy){

			@Override
			protected void setup() {
			}};
			
		layoutDesc.setText("<form><line label=\"label\" type=\"String\"/></form>");
		addNorth(layoutDesc, 400);
		add(form);
		
		layoutDesc.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				SRV.configurationService.parse(layoutDesc.getText(), new DefaultCallback<FormDesc>() {

					@Override
					public void onSuccess(FormDesc result) {
						
					}
				});
			}
		});
	}



}

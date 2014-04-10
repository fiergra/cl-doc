package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.HumanBeingListBox;
import com.ceres.cldoc.model.IApplication;
import com.ceres.cldoc.model.IEntity;
import com.ceres.cldoc.model.Person;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorWidgetLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class HumanBeingSelectorFactory implements ILinkFactory {
	private final IApplication application;
	
	public HumanBeingSelectorFactory(final IApplication application) {
		this.application = application;
	}
	@Override
	public InteractorWidgetLink createLink(final Interactor interactor, final String fieldName,
			HashMap<String, String> attributes) {
		final HumanBeingListBox hbl = new HumanBeingListBox(application, null);
		final InteractorWidgetLink link = new InteractorWidgetLink(interactor, fieldName, hbl, attributes) {
			
			private boolean isSelecting = false;
			
			@Override
			public void toDialog(Map<String, Serializable> item) {
				Long selectedId = (Long) get(item, fieldName);
				
				if (selectedId != null) {
					isSelecting = true;
					SRV.humanBeingService.findById(application.getSession(), selectedId, new DefaultCallback<Person>(application, "find person by id") {

						@Override
						public void onResult(Person result) {
							hbl.setSelected(result);
							isSelecting = false;
						}
					});
				} else {
					hbl.setSelected(null);
				}
			}
			
			@Override
			public void fromDialog(Map<String, Serializable> item) {
				IEntity e = hbl.getSelected();
				item.put(fieldName, e != null ? e.getId() : null);
			}

			@Override
			public boolean isEmpty() {
				return hbl.getSelected() == null && !isSelecting;
			}
		};
		
		hbl.addSelectionChangedHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(link);
			}
		});

		return link;
	}

}

package com.ceres.cldoc.client.views.dynamicforms;

import java.util.HashMap;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.HumanBeingListBox;
import com.ceres.cldoc.model.Person;
import com.ceres.core.IApplication;
import com.ceres.core.IEntity;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.INamedValues;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class HumanBeingSelectorFactory implements ILinkFactory {

	@Override
	public InteractorLink createLink(final IApplication application, final Interactor interactor, String fieldName,
			HashMap<String, String> attributes) {
		final HumanBeingListBox hbl = new HumanBeingListBox(application, null);
		final InteractorLink link = new InteractorLink(interactor, fieldName, hbl, attributes) {
			
			@Override
			public void toDialog(INamedValues item) {
				Long id = (Long) item.getValue(fieldName);
				
				if (id != null) {
					SRV.humanBeingService.findById(application.getSession(), id, new DefaultCallback<Person>() {

						@Override
						public void onSuccess(Person result) {
							hbl.setSelected(result);
						}
					});
				} else {
					hbl.setSelected(null);
				}
			}
			
			@Override
			public void fromDialog(INamedValues item) {
				IEntity e = hbl.getSelected();
				item.setValue(fieldName, e != null ? e.getId() : null);
			}

			@Override
			public boolean isEmpty() {
				return hbl.getSelected() == null;
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

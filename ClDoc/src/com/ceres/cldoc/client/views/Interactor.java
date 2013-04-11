package com.ceres.cldoc.client.views;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.controls.DateTextBox;
import com.ceres.cldoc.client.controls.FloatTextBox;
import com.ceres.cldoc.client.controls.LongTextBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBoxBase;

public class Interactor implements IView {
	private final Session session;
	private final IAct act;
	private final HashMap<String, InteractorLink> links = new HashMap<String, InteractorLink>();
	private final Runnable setModified;

	public Interactor(Session session, IAct act) {
		this(session, act, null);
	}
	
	public Interactor(Session session, IAct act, Runnable setModified) {
		this.session = session;
		this.act = act;
		this.setModified = setModified;
	}

	private boolean validate(TextBoxBase textBox) {
		boolean isValid = false;
		String sValue = textBox.getText();
		if (sValue == null || sValue.length() == 0) {
			textBox.addStyleName("invalidContent");
		} else {
			textBox.removeStyleName("invalidContent");
			isValid = true;
		}

		return isValid;
	}
	

	
	public void toDialog() {
		Iterator<Entry<String, InteractorLink>> iter = links.entrySet().iterator();

		while (iter.hasNext()) {
			final InteractorLink link = iter.next().getValue();

			switch (link.dataType) {
			case FT_TEXT:
			case FT_STRING:
				String sValue = act.getString(link.name);
				((TextBoxBase) link.widget).setText(sValue);
				if (link.isMandatory) {
					validate((TextBoxBase) link.widget);
				}
				break;
			case FT_BOOLEAN:
				((CheckBox) link.widget).setValue(act.getBoolean(link.name));
				break;
			case FT_MULTI_SELECTION:
				((CatalogMultiSelect)link.widget).setSelected(act.getCatalogList(link.name));
				break;
			case FT_OPTION_SELECTION:
			case FT_LIST_SELECTION:
				Catalog catalog = act.getCatalog(link.name);
				if (catalog != null) {
					((IEntitySelector<Catalog>)link.widget).setSelected(catalog);
				}
				break;
			case FT_PARTICIPATION:
				IAssignedEntitySelector<Entity> selector = (IAssignedEntitySelector<Entity>)link.widget;
				Participation participation = act.getParticipation(selector.getRole());
				selector.setSelected(participation != null ? participation.entity : null);
				break;
			case FT_HUMANBEING:
				Long id = act.getLong(link.name);
				if (id != null) {
					SRV.humanBeingService.findById(session, id, new DefaultCallback<Person>() {

						@Override
						public void onSuccess(Person result) {
							((IEntitySelector<Person>)link.widget).setSelected(result);
						}
					});
					
				}
				break;
			case FT_DATE:
				Date value = act.getDate(link.name);
				if (value != null) {
					((DateTextBox) link.widget).setDate(value);
				}
				break;
			case FT_ACTDATE:
				if (act.getDate() != null) {
					((DateTextBox) link.widget).setDate(act.getDate());
				}
				break;
			case FT_FLOAT:
				((FloatTextBox) link.widget).setFloat(act.getFloat(link.name));
				break;
			case FT_INTEGER:
				((LongTextBox) link.widget).setLong(act.getLong(link.name));
				break;
//			case FT_TIME:
//				Date dateValue = act.getDate(field.name);
//				if (dateValue != null) {
//					((TimeTextBox) field.widget).setTime(dateValue);
//				}
//				break;
			case FT_IMAGE:
				break;
			case FT_SEPARATOR:
				break;
			case FT_UNDEF:
				break;
			default:
				break;
			}
		}
	}

	public void fromDialog() {
		Iterator<Entry<String, InteractorLink>> iter = links.entrySet().iterator();

		while (iter.hasNext()) {
			InteractorLink field = iter.next().getValue();
			String qualifiedFieldName = field.name;

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				act.set(qualifiedFieldName,
						((TextBoxBase) field.widget).getText());
				break;
			case FT_BOOLEAN:
				act.set(qualifiedFieldName, ((CheckBox) field.widget).getValue());
				break;
			case FT_MULTI_SELECTION:
				act.set(qualifiedFieldName, ((IEntitySelector<CatalogList>) field.widget).getSelected());
				break;
			case FT_OPTION_SELECTION:
			case FT_LIST_SELECTION:
				Catalog catalog = ((IEntitySelector<Catalog>) field.widget).getSelected(); 
				act.set(qualifiedFieldName, catalog != null ? catalog : null);
				break;
			case FT_PARTICIPATION:
				IAssignedEntitySelector<Person> selector = (IAssignedEntitySelector<Person>) field.widget;
				Person humanBeing = selector.getSelected(); 
				Participation p = act.getParticipation(selector.getRole());
				
				if (p == null || !p.entity.equals(humanBeing)) {
					act.setParticipant(humanBeing, selector.getRole());
				}
				
				break;
			case FT_HUMANBEING:
				humanBeing = ((IEntitySelector<Person>) field.widget).getSelected(); 
				act.set(qualifiedFieldName, humanBeing != null ? humanBeing.id : null);
				break;
			case FT_DATE:
				act.set(qualifiedFieldName,
						((DateTextBox) field.widget).getDate());
				break;
			case FT_ACTDATE:
				act.setDate(((DateTextBox) field.widget).getDate());
				break;
			case FT_FLOAT:
				act.set(qualifiedFieldName,
						((FloatTextBox) field.widget).getFloat());
				break;
			case FT_INTEGER:
				act.set(qualifiedFieldName,
						((LongTextBox) field.widget).getLong());
				break;
//			case FT_TIME:
//				act.set(qualifiedFieldName,
//						((TimeTextBox) field.widget).getDate());
//				break;
			}
		}

	}

	@Override
	public IAct getModel() {
		return act;
	}

	private boolean isModified = false;

	private class WidgetKeyUpHandler implements KeyUpHandler {

		private final IsWidget widget;

		public WidgetKeyUpHandler(IsWidget widget) {
			this.widget = widget;
		}
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			onModification();
			if (widget instanceof TextBoxBase) {
				validate((TextBoxBase) widget);
//				System.out.print(((TextBoxBase) widget).getText());
			}
			
			
			if (!isModified) {
				isModified = true;
				if (setModified != null) {
					setModified.run();
				}
			}
		}
		
	}
	
	protected void onModification() {}
	
	@Override
	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public void clearModification() {
		isModified = false;
	}

	public void addLink(String fieldName, InteractorLink interactorLink) {
		links.put(fieldName, interactorLink);
		if (interactorLink.widget instanceof TextBoxBase) {
			((TextBoxBase)interactorLink.widget).addKeyUpHandler(new WidgetKeyUpHandler(interactorLink.widget));
		} else if (interactorLink.widget instanceof ButtonBase) {
			((ButtonBase)interactorLink.widget).addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			
		} else if (interactorLink.widget instanceof ListBox) {
			((ListBox)interactorLink.widget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			
		} else if (interactorLink.widget instanceof IEntitySelector) {
			((IEntitySelector)interactorLink.widget).addSelectionChangedHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			
		}
		
		
		
	}
}

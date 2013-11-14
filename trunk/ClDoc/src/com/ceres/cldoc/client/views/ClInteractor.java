package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.Form.DataType;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.ceres.core.ISession;
import com.ceres.dynamicforms.client.components.DateTextBox;
import com.ceres.dynamicforms.client.components.FloatTextBox;
import com.ceres.dynamicforms.client.components.LongTextBox;
import com.ceres.dynamicforms.client.components.TimeTextBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class ClInteractor implements IView {
	private final ISession session;
	private IAct act;
	private final HashMap<String, ClInteractorLink> links = new HashMap<String, ClInteractorLink>();
	private Runnable modificationCallback;
	private ValidationCallback validationCallback;
	private boolean isModified = false;
	private boolean isValid = true;
	private Runnable anyModificationCallback;
	private boolean deleted = false;

	
	public ClInteractor(ISession session, IAct act) {
		this(session, act, null, null);
	}
	
	public ClInteractor(ISession session, IAct act, Runnable setModified, ValidationCallback setValid) {
		this.session = session;
		this.act = act;
		this.modificationCallback = setModified;
		this.validationCallback = setValid;
	}

	private int getMaxCol(HashMap<String, String> attributes) {
		String sMax = attributes.get("columns");
		return sMax != null ? Integer.valueOf(sMax) : 6;
	}


	public Widget createWidgetForType(ClDoc clDoc, DataType dataType, boolean required, HashMap<String, String> attributes) {
		Widget widget = null;

		switch (dataType) {
		case FT_STRING:
			TextBox t = new TextBox();
			widget = t;
			widget.setWidth("10em");
			break;
		case FT_YESNO:
			YesNoRadioGroup yng = new YesNoRadioGroup();
			widget = yng;
			break;
		case FT_BOOLEAN:
			CheckBox c = new CheckBox();
			widget = c;
			break;
		case FT_LIST_SELECTION:
			CatalogListBox clb = new CatalogListBox(clDoc, attributes.get("parent"));
			widget = clb;
			widget.setWidth("60%");
			break;
		case FT_OPTION_SELECTION:
			CatalogRadioGroup crg = new CatalogRadioGroup(clDoc, attributes.get("parent"), attributes.get("orientation"));
			widget = crg;
			break;
		case FT_MULTI_SELECTION:
			CatalogMultiSelect cms = new CatalogMultiSelect(clDoc, attributes.get("parent"), getMaxCol(attributes), attributes.get("orientation"));
			widget = cms;
			break;
		case FT_PARTICIPATION:
			HumanBeingListBox hlb = new HumanBeingListBox(clDoc, attributes.get("role"));
			widget = hlb;
			widget.setWidth("60%");
			break;
		case FT_HUMANBEING:
			hlb = new HumanBeingListBox(clDoc, attributes.get("role"));
			widget = hlb;
			widget.setWidth("60%");
			break;
		case FT_TEXT:
			TextArea a = new TextArea();
			a.setWidth("99%");
			widget = a;
			break;
		case FT_ACTDATE:
		case FT_DATE:
			DateTextBox d = new DateTextBox();
			d.setWidth("10em");
			widget = d;
			break;
		case FT_PARTICIPATION_TIME:	
			TimeTextBox ptimeBox = new TimeTextBox();
			ptimeBox.setWidth("10em");
			widget = ptimeBox;
			break;
		case FT_TIME:
			TimeTextBox timeBox = new TimeTextBox();
			timeBox.setWidth("10em");
			widget = timeBox;
			break;
		case FT_FLOAT:
			FloatTextBox f = new FloatTextBox();
			f.setWidth("5em");
			widget = f;
			break;
		case FT_INTEGER:
			LongTextBox itb = new LongTextBox();
			itb.setWidth("5em");
			widget = itb;
			break;
		case FT_IMAGE:
			String source = attributes.get("source");
			Image img = new Image("icons/" + source);
			widget = img;
			break;
		case FT_SEPARATOR:
			String title = attributes.get("title");
			String html = "<hr noshade=\"noshade\" size=\"1\"/>";
			if (title != null) {
				html = "</br></br><div class=\"formSectionTitle\"><b>" + title + "</b></div>" + html;
			}
			widget = new HTML(html);
			break;
//		case FT_TIME:
//			TimeTextBox tbx = new TimeTextBox();
//			tbx.setWidth("4em");
//			tbx.addKeyDownHandler(modificationHandler);
//			w = tbx;
//			break;
		default:
			widget = new TextBox();
			break;

		}
		
		if (widget != null) {
			if (attributes != null) {
				String sWidth = attributes.get("width");
				String sHeight = attributes.get("height");
				if (sWidth != null) {
					widget.setWidth(sWidth);
				}
				if (sHeight != null) {
					widget.setHeight(sHeight);
				}
			}
			if (required) {
				widget.addStyleName("required");
			}
		}
		
		return widget;
	}


	private Catalog getRole(String role) {
		if (Participation.ADMINISTRATOR.code.equalsIgnoreCase(role)) {
			return Participation.ADMINISTRATOR;
		} else if (Participation.PROTAGONIST.code.equalsIgnoreCase(role)) {
			return Participation.PROTAGONIST;
		} else if (Participation.ORGANISATION.code.equalsIgnoreCase(role)) {
			return Participation.ORGANISATION;
		}
		
		return null;
	}


	@Override
	public void toDialog() {
		Iterator<Entry<String, ClInteractorLink>> iter = links.entrySet().iterator();

		while (iter.hasNext()) {
			final ClInteractorLink link = iter.next().getValue();
			boolean validateSynchronously = true;
			
			switch (link.dataType) {
			case FT_TEXT:
			case FT_STRING:
				String sValue = act.getString(link.name);
				((TextBoxBase) link.widget).setText(sValue);
//				link.validate();
				break;
			case FT_BOOLEAN:
				((CheckBox) link.widget).setValue(act.getBoolean(link.name));
				break;
			case FT_YESNO:
				((YesNoRadioGroup) link.widget).setValue(act.getBoolean(link.name), false);
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
				validateSynchronously = false;
				Long id = act.getLong(link.name);
				if (id != null) {
					SRV.humanBeingService.findById(session, id, new DefaultCallback<Person>() {

						@Override
						public void onSuccess(Person result) {
							((IEntitySelector<Person>)link.widget).setSelected(result);
							if (validationCallback != null) {
								setValid(link, link.validate());
//								validationCallback.setValid(link, link.validate());
							}
						}
					});
					
				} else {
					if (validationCallback != null) {
						setValid(link, link.validate());
					}

				}
				break;
			case FT_DATE:
			case FT_TIME:
				Date value = act.getDate(link.name);
				if (value != null) {
					((DateTextBox) link.widget).setDate(value);
				}
//				link.validate();
				break;
			case FT_PARTICIPATION_TIME:
				Catalog role = getRole(link.attributes.get("role"));
				Participation part = act.getParticipation(role);
				if (part != null) {
					((TimeTextBox) link.widget).setDate("start".equals(link.attributes.get("which")) ? part.start : part.end);
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
			
			if (validateSynchronously && validationCallback != null) {
				setValid(link, link.validate());
//				validationCallback.setValid(link, link.validate());
			}
		}
	}

	@Override
	public void fromDialog() {
		Iterator<Entry<String, ClInteractorLink>> iter = links.entrySet().iterator();

		while (iter.hasNext()) {
			ClInteractorLink field = iter.next().getValue();
			String qualifiedFieldName = field.name;
			Serializable value = field.getValue();
			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
			case FT_YESNO:
			case FT_BOOLEAN:
			case FT_MULTI_SELECTION:
			case FT_OPTION_SELECTION:
			case FT_TIME:
			case FT_DATE:
			case FT_ACTDATE:
			case FT_FLOAT:
			case FT_INTEGER:
			case FT_LIST_SELECTION:
				act.set(qualifiedFieldName, value);
				break;
			case FT_HUMANBEING:
				Person humanBeing = (Person)value; 
				act.set(qualifiedFieldName, humanBeing != null ? humanBeing.getId() : null);
				break;
			case FT_PARTICIPATION:
				IAssignedEntitySelector<Person> selector = (IAssignedEntitySelector<Person>) field.widget;
				humanBeing = selector.getSelected(); 
				Participation p = act.getParticipation(selector.getRole());
				
				if (p == null || !p.entity.equals(humanBeing)) {
					act.setParticipant(humanBeing, selector.getRole());
				}
				
				break;
			case FT_PARTICIPATION_TIME:
				Catalog role = getRole(field.attributes.get("role"));
				Participation part = act.getParticipation(role);
				if (part != null) {
					Date pDate = ((TimeTextBox) field.widget).getDate();
					if ("start".equals(field.attributes.get("which"))) {
						part.start = pDate;
					} else {
						part.end = pDate;
					}
				}
				break;
			}
		}

	}

	@Override
	public IAct getModel() {
		return act;
	}

	private class ButtonClickHandler extends InteractorHandler implements ClickHandler {
		
		public ButtonClickHandler(ClInteractorLink link) {
			super(link);
		}

		@Override
		public void onClick(ClickEvent event) {
			link.interactor.setModification();
			link.interactor.setValid(link, link.validate());
		}
	}
	
//	private class TextBoxKeyUpHandler extends InteractorHandler implements KeyUpHandler {
//
//		public TextBoxKeyUpHandler(InteractorLink link) {
//			super(link);
//		}
//		
//		@Override
//		public void onKeyUp(KeyUpEvent event) {
//			link.interactor.setModification();
//			link.interactor.setValid(link, link.validate());
//		}
//		
//	}

	
	private class TextBoxChangeHandler extends InteractorHandler implements ChangeHandler {

		public TextBoxChangeHandler(ClInteractorLink link) {
			super(link);
		}
		
		@Override
		public void onChange(ChangeEvent event) {
			link.interactor.setModification();
			link.interactor.setValid(link, link.validate());
		}
		
	}
	
	
	private class EntitySelectorChangeHandler extends InteractorHandler implements ChangeHandler {

		public EntitySelectorChangeHandler(ClInteractorLink interactorLink) {
			super(interactorLink);
		}

		@Override
		public void onChange(ChangeEvent event) {
			link.interactor.setModification();
			link.interactor.setValid(link, link.validate());
		}
		
	}
	
	protected void onAnyModification() {
		if (anyModificationCallback != null) {
			anyModificationCallback.run();
		}
	}
	
	public void setValid(ClInteractorLink link, boolean validated) {
		boolean wasValid = isValid;
		if (isValid) {
			isValid = isValid && validated;
		} else {
			Iterator<ClInteractorLink> i = links.values().iterator();
			isValid = true;
			while (isValid && i.hasNext()) {
				isValid = isValid && i.next().validate();
			}
		}
		
		if (validationCallback != null && isValid != wasValid) {
			validationCallback.setValid(link, validated);
		}
	}
//
//	private void validateAll() {
//		Iterator<InteractorLink> i = links.values().iterator();
//		isValid = true;
//		while (i.hasNext()) {
//			InteractorLink link = i.next();
//			boolean validated = link.validate();
//			isValid = isValid && validated;
//			setValid.setValid(link, validated);
//		}
//	}

	public void setModification() {
		onAnyModification();
		if (!isModified) {
			isModified = true;
			if (modificationCallback != null) {
				modificationCallback.run();
			}
		}
	}

	@Override
	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public void clearModification() {
		isModified = false;
	}

	public void addLink(String fieldName, final ClInteractorLink interactorLink) {
		links.put(fieldName, interactorLink);
		if (interactorLink.widget instanceof DateTextBox) {
			((DateTextBox)interactorLink.widget).addDateChangeHandler(new ValueChangeHandler<Date>() {

				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					interactorLink.interactor.setModification();
					interactorLink.interactor.setValid(interactorLink, interactorLink.validate());
				}
			});
		} else if (interactorLink.widget instanceof TextBoxBase) {
			((TextBoxBase)interactorLink.widget).addChangeHandler(new TextBoxChangeHandler(interactorLink));
		} else if (interactorLink.widget instanceof YesNoRadioGroup) {
			((YesNoRadioGroup)interactorLink.widget).addChangeHandler(new EntitySelectorChangeHandler(interactorLink));
		} else if (interactorLink.widget instanceof IEntitySelector) {
			((IEntitySelector)interactorLink.widget).addSelectionChangedHandler(new EntitySelectorChangeHandler(interactorLink));
		} else if (interactorLink.widget instanceof ButtonBase) {
			((ButtonBase)interactorLink.widget).addClickHandler(new ButtonClickHandler(interactorLink));
		} else if (interactorLink.widget instanceof ListBox) {
			((ListBox)interactorLink.widget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						modificationCallback.run();
					}
				}
			});
			
		}
		
		
		
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValidationCallback(ValidationCallback validationCallback) {
		this.validationCallback = validationCallback;
	}

	public boolean isEmpty() {
		boolean isEmpty = true;
		Iterator<ClInteractorLink> iter = links.values().iterator();
		while (isEmpty && iter.hasNext()) {
			isEmpty = isEmpty && iter.next().getValue() == null;
		}
		return isEmpty;
	}

	public void setModificationCallback(Runnable modificationCallback) {
		this.modificationCallback = modificationCallback;
	}

	public void setAnyModificationCallback(Runnable anyModificationCallback) {
		this.anyModificationCallback = anyModificationCallback;
	}

	public void setModel(Act result) {
		this.act = result;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
}

package com.ceres.cldoc.client.views;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public abstract class Form <T extends ValueBag> extends VerticalPanel {
	protected T model;
	protected DateTimeFormat df = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
	
	final static int OK = 1;
	final static int CLOSE = 2;
	final static int CANCEL = 4;
	private static final int LABELWIDTH = 110;
	private static final int SPACING = 3;
	
	public static final int STRING = 1;
	public static final int DATE = 2;

	public Form(T model) {
		this.model = model;
		setup();
		toDialog();
	}

	private static class Field {
		public String name;
		public Widget widget;
		public int dataType;
		
		public Field(String name, Widget widget, int dataType) {
			super();
			this.name = name;
			this.widget = widget;
			this.dataType = dataType;
		}
		
		
	}
	
	private HashMap <String, Field> fields = new HashMap<String, Field>();

//	private ValueBag getValueBag(ValueBag valueBag, Field field) {
//		int index = field.name.indexOf('.');
//		
//		if (index == -1) {
//			return valueBag;
//		} else {
//			String vbName = field.name.substring(0, index);
//			Field childField = new Field(field.name.substring(index + 1), field.widget, field.dataType);
//			return getValueBag(valueBag.getValueBag(vbName), childField);
//		}
//	}
//	
	protected void toDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();
		
		while (iter.hasNext()) {
			Field field = iter.next().getValue();
			ValueBag valueBag = model;//getValueBag(model, field);

			switch (field.dataType) {
			case STRING:
				((TextBox)field.widget).setText(valueBag.getString(field.name));
				break;
			case DATE:
				Date value = valueBag.getDate(field.name);
				if (value != null) {
					((DatePicker)field.widget).setValue(value);
					((DatePicker)field.widget).setCurrentMonth(value);
				}
				break;
			}
		}
	}
	
	protected void fromDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			Field field = iter.next().getValue();
			String qualifiedFieldName = field.name;
			ValueBag valueBag = model;//getValueBag(model, field);
			
			switch (field.dataType) {
			case STRING:
				valueBag.set(qualifiedFieldName, ((TextBox)field.widget).getText());
				break;
			case DATE:
				valueBag.set(qualifiedFieldName, ((DatePicker)field.widget).getValue());
				break;
			}
		}
		
	}

	private PopupPanel popup;
	
	protected abstract void setup();
	
	private HorizontalPanel addButtons(final OnClick onClickSave, final OnClick onClickDelete, final OnClick onClickCancel) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);
//		hp.setWidth("100%");
//		hp.setHorizontalAlignment(ALIGN_RIGHT);

		if (onClickSave != null) {
			final Button pbOk = new Button("Save");
			hp.add(pbOk);
			pbOk.setStylePrimaryName("button");
			pbOk.addStyleName("gray");
			pbOk.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					fromDialog();
					onClickSave.onClick(popup);
				}
			});
		}
		
		if (onClickCancel != null) {
			final Button pbCancel = new Button("Cancel");
			hp.add(pbCancel);
			pbCancel.setStylePrimaryName("button");
			pbCancel.addStyleName("gray");
			pbCancel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onClickCancel.onClick(popup);
				}
			});
		}		
		
		if (onClickDelete != null) {
			final Button pbCancel = new Button("Delete");
			hp.add(pbCancel);
			pbCancel.setStylePrimaryName("button");
			pbCancel.addStyleName("gray");
			pbCancel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onClickDelete.onClick(popup);
				}
			});
		}		
		
		return hp;
	}
	
	public static class DefaultOnClickClose implements OnClick {

		@Override
		public void onClick(PopupPanel pp) {
			pp.hide();
		}
		
	}

	public void showModal(String title, OnClick onClickOk) {
		showModal(title, onClickOk, new DefaultOnClickClose(), new DefaultOnClickClose());
	}	
	
	public void showModal(String title, OnClick onClickSave, OnClick onClickDelete, OnClick onClickCancel) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(this);
		vp.add(new HTML("<hr width=\"100%\">"));
		HorizontalPanel buttons = addButtons(onClickSave, onClickDelete, onClickCancel);
		vp.add(buttons);
		
		popup = PopupManager.showModal(title, vp);
	}
	
	protected void close() {
		if (popup != null) {
			popup.hide();
		}
	}
	
	protected void addLine(String label, Widget ... widgets) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(SPACING);
		hp.setWidth("500px");
		hp.setHorizontalAlignment(ALIGN_LEFT);
		Label l = new Label(label);
		l.setWidth(LABELWIDTH + "px");
		hp.add(l);
		for (Widget widget : widgets) {
			hp.add(widget);
		}
		
		add(hp);
	}

	
	protected FocusWidget addLine(String labelText, String fieldName, int dataType, int width, boolean focused) {
		FocusWidget w = addLine(labelText, fieldName, dataType, width);
		w.setFocus(focused);
		return w;
	}

	protected FocusWidget addLine(String labelText, String fieldName, int dataType, int width) {
		FocusWidget w = addLine(labelText, fieldName, dataType);
		w.setWidth(width + "em");
		return w;
	}

	protected FocusWidget addLine(String labelText, String fieldName, int dataType) {
		FocusWidget widget = createWidgetForType(dataType);
		addLine(labelText, widget);
		fields.put(fieldName, new Field(fieldName, widget, dataType));
		return widget;
	}

	
	
	private FocusWidget createWidgetForType(int dataType) {
		FocusWidget w = null;
		
		switch (dataType) {
		case STRING: 
			w = new TextBox(); 
			break;
//		case DATE: 
//			DatePicker dp = new DatePicker(); 
//			w = dp;
//			break;
		default: 
			w = new TextBox(); 
			break;
		
		}
		
		return w;
	}
	
}

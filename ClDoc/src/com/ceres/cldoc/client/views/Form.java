package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.client.controls.DateTextBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.Catalog;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.INamedValueAccessor;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;


public abstract class Form<T extends INamedValueAccessor> extends FlexTable {

	public enum DataTypes {
		FT_STRING, FT_TEXT, FT_DATE, FT_INTEGER, FT_LIST_SELECTION, FT_BOOLEAN, FT_HUMANBEING
	};

	protected T model;
	protected DateTimeFormat df = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);
	private Runnable setModified;

	final static int OK = 1;
	final static int CLOSE = 2;
	final static int CANCEL = 4;
	private static final int SPACING = 3;

	public Form(T model, Runnable setModified) {
		addStyleName("form");
		this.model = model;
		this.setModified = setModified;
		this.setColumnFormatter(new ColumnFormatter(){

			@Override
			public String getStyleName(int column) {
				return column == 0 ? "formLabel" : super.getStyleName(column);
			}});
		setup();
		toDialog();
	}

	private static class Field {
		public String name;
		public Widget widget;
		public DataTypes dataType;

		public Field(String name, Widget widget, DataTypes dataType) {
			super();
			this.name = name;
			this.widget = widget;
			this.dataType = dataType;
		}

	}

	private HashMap<String, Field> fields = new HashMap<String, Field>();

	public void toDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			final Field field = iter.next().getValue();
			INamedValueAccessor valueBag = model;// getValueBag(model, field);

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				((TextBoxBase) field.widget)
						.setText(valueBag.getString(field.name));
				break;
			case FT_BOOLEAN:
				((CheckBox) field.widget).setValue((Boolean) valueBag.get(field.name));
				break;
			case FT_LIST_SELECTION:
				Catalog catalog = (Catalog) valueBag.get(field.name);
				if (catalog != null) {
					((IEntitySelector<Catalog>)field.widget).setSelected(catalog);
				}
//				String code = valueBag.getString(field.name);
//				if (code != null) {
//					SRV.configurationService.getCatalog(code, new DefaultCallback<Catalog>() {
//
//						@Override
//						public void onSuccess(Catalog result) {
//							((IEntitySelector<Catalog>)field.widget).setSelected(result);
//						}
//					});
//					
//				}
				break;
			case FT_HUMANBEING:
				Long id = valueBag.getLong(field.name);
				if (id != null) {
					SRV.humanBeingService.findById(id, new DefaultCallback<HumanBeing>() {

						@Override
						public void onSuccess(HumanBeing result) {
							((IEntitySelector<HumanBeing>)field.widget).setSelected(result);
						}
					});
					
				}
				break;
			case FT_DATE:
				Date value = valueBag.getDate(field.name);
				if (value != null) {
					((DateTextBox) field.widget).setDate(value);
				}
				break;
			}
		}
	}

	public void fromDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			Field field = iter.next().getValue();
			String qualifiedFieldName = field.name;
			INamedValueAccessor valueBag = model;// getValueBag(model, field);

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				valueBag.set(qualifiedFieldName,
						((TextBoxBase) field.widget).getText());
				break;
			case FT_BOOLEAN:
				valueBag.set(qualifiedFieldName, ((CheckBox) field.widget).getValue());
				break;
			case FT_LIST_SELECTION:
				Catalog catalog = ((IEntitySelector<Catalog>) field.widget).getSelected(); 
				valueBag.set(qualifiedFieldName, catalog != null ? catalog : null);
				break;
			case FT_HUMANBEING:
				HumanBeing humanBeing = ((IEntitySelector<HumanBeing>) field.widget).getSelected(); 
				valueBag.set(qualifiedFieldName, humanBeing != null ? humanBeing.id : null);
				break;
			case FT_DATE:
				valueBag.set(qualifiedFieldName,
						((DateTextBox) field.widget).getDate());
				break;
			}
		}

	}

	private PopupPanel popup;
	private boolean isModified = false;
	private int row;

	private KeyDownHandler modificationHandler = new KeyDownHandler() {

		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (!isModified) {
				isModified = true;
				if (setModified != null) {
					setModified.run();
				}
			}
		}
	};

	public boolean isModified() {
		return isModified;
	}
	
	public void clearModification() {
		isModified = false;
	}

	protected abstract void setup();

	private HorizontalPanel addButtons(final OnClick<T> onClickSave,
			final OnClick<T> onClickDelete, final OnClick<T> onClickCancel) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		if (onClickSave != null) {
			final Button pbOk = new Button("Save");
			hp.add(pbOk);
			pbOk.setStylePrimaryName("button");
			pbOk.addStyleName("gray");
			pbOk.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					fromDialog();
					onClickSave.onClick(model);
					popup.hide();
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
					onClickCancel.onClick(model);
					popup.hide();
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
					onClickDelete.onClick(model);
					popup.hide();
				}
			});
		}

		return hp;
	}

	public void showModal(String title, OnClick<T> onClickSave, OnClick<T> onClickDelete, OnClick<T> onClickCancel) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(this);
		vp.add(new HTML("<hr width=\"100%\">"));
		HorizontalPanel buttons = addButtons(onClickSave, onClickDelete, onClickCancel);
		vp.add(buttons);

		popup = PopupManager.showModal(title, vp);
	}

	public void close() {
		if (popup != null) {
			popup.hide();
		}
	}

	protected void addLine(String label, Widget... widgets) {
		Label l = new Label(label);
		l.setStylePrimaryName("formLabel");
		setWidget(row, 0, l);
		Widget w;

		if (widgets.length > 1) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.setSpacing(SPACING);
			for (Widget widget : widgets) {
				hp.add(widget);
			}
			w = hp;
		} else {
			w = widgets[0];
		}
		setWidget(row, 1, w);
		getFlexCellFormatter().setAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		row++;
	}

	protected Widget addLine(String labelText, String fieldName, DataTypes dataType, int width, boolean focused) {
		Widget w = addLine(labelText, fieldName, dataType, width);
		if (w instanceof Focusable) {
			((Focusable)w).setFocus(focused);
		}
		return w;
	}

	protected Widget addLine(String labelText, String fieldName,
			DataTypes dataType, int width) {
		Widget w = addLine(labelText, fieldName, dataType, null);
		w.setWidth(width + "em");
		return w;
	}

	protected Widget addLine(String labelText, String fieldName,
			DataTypes dataType) {
		return addLine(labelText, fieldName, dataType, null);
	}

	protected Widget addLine(String labelText, String fieldName,
			DataTypes dataType, HashMap <String, String> attributes) {
		Widget widget = createWidgetForType(dataType, attributes);
		addLine(labelText, widget);
		fields.put(fieldName, new Field(fieldName, widget, dataType));

		return widget;
	}

	private Widget createWidgetForType(DataTypes dataType, HashMap<String, String> attributes) {
		Widget w = null;

		switch (dataType) {
		case FT_STRING:
			TextBox t = new TextBox();
			t.addKeyDownHandler(modificationHandler);
			w = t;
			break;
		case FT_BOOLEAN:
			CheckBox c = new CheckBox();
			c.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = c;
			break;
		case FT_LIST_SELECTION:
			CatalogListBox clb = new CatalogListBox(attributes.get("parent"));
			clb.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = clb;
			break;
		case FT_HUMANBEING:
			HumanBeingListBox hlb = new HumanBeingListBox();
			hlb.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = hlb;
			break;
		case FT_TEXT:
			TextArea a = new TextArea();
			a.setWidth("100%");
			a.addKeyDownHandler(modificationHandler);
			w = a;
			break;
		case FT_DATE:
			DateTextBox d = new DateTextBox();
			d.setWidth("10em");
			d.addKeyDownHandler(modificationHandler);
			w = d;
			break;
		default:
			w = new TextBox();
			break;

		}

		return w;
	}
	
	protected void parseAndCreate(String xml) {
		SRV.configurationService.parse(xml, new DefaultCallback<LayoutElement>() {

			@Override
			public void onSuccess(LayoutElement result) {
				if (result.getChildren() != null && !result.getChildren().isEmpty()) {
					clear();
					createAndLayout(result.getChildren().get(0));
					toDialog();
				}
			}
		});
	}


	protected void createAndLayout(LayoutElement layoutElement) {
		ArrayList<LayoutElement> children = layoutElement.getChildren();
		for (LayoutElement child : children) {
			if (child.getType().equals("form")) {
				
			} else if (child.getType().equals("line")) {
				String label = child.getAttribute("label");
				String fieldName = child.getAttribute("name");
				addLine(label == null ? fieldName : label, fieldName, getDataType(child), child.getAttributes());
			}
		}
		
	}


	private DataTypes getDataType(LayoutElement child) {
		String type = child.getAttribute("type");
		DataTypes result = DataTypes.FT_STRING;
		
		if (type != null) {
			type = type.toLowerCase();
	
			if (type.equals("string")) {
				result = DataTypes.FT_STRING;
			} else if (type.equals("text")) {
				result = DataTypes.FT_TEXT;
			} else if (type.equals("date")) {
				result = DataTypes.FT_DATE;
			} else if (type.equals("boolean")) {
				result = DataTypes.FT_BOOLEAN;
			} else if (type.equals("catalog")) {
				result = DataTypes.FT_LIST_SELECTION;
			} else if (type.equals("humanbeing")) {
				result = DataTypes.FT_HUMANBEING;
			}
		}		
		
		return result;
	}

	

}

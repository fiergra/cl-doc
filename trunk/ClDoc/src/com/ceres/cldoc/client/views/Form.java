package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.DateTextBox;
import com.ceres.cldoc.client.controls.FloatTextBox;
import com.ceres.cldoc.client.controls.OnDemandChangeListener;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public abstract class Form<T extends IAct> extends FlexTable implements IView<T>{

	public enum DataTypes {
		FT_STRING, FT_TEXT, FT_DATE, /*FT_TIME, */FT_INTEGER, FT_FLOAT, FT_LIST_SELECTION, FT_OPTION_SELECTION, FT_MULTI_SELECTION, FT_BOOLEAN, FT_HUMANBEING, FT_UNDEF
	};

	protected T model;
	protected DateTimeFormat df = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);
	private final Runnable setModified;
	private final ClDoc clDoc;

	private final Collection<Form> pages = new ArrayList<Form>();
	
	final static int OK = 1;
	final static int CLOSE = 2;
	final static int CANCEL = 4;
	private static final int SPACING = 3;

	public Form(ClDoc clDoc, T model, Runnable setModified) {
		this.clDoc = clDoc;
		addStyleName("docform");
		setRowFormatter(new RowFormatter() {});
		this.model = model;
		this.setModified = setModified;
		setup();
		toDialog();
	}

	
	
	@Override
	public void clear() {
		super.clear();
		pages.clear();
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

	private final HashMap<String, Field> fields = new HashMap<String, Field>();

	@Override
	@SuppressWarnings("unchecked")
	public void toDialog() {
		for (Form<T> page:pages) {
			page.toDialog();
		}

		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			final Field field = iter.next().getValue();
			IAct act = model;// getAct(model, field);

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				((TextBoxBase) field.widget)
						.setText(act.getString(field.name));
				break;
			case FT_BOOLEAN:
				((CheckBox) field.widget).setValue(act.getBoolean(field.name));
				break;
			case FT_MULTI_SELECTION:
				((CatalogMultiSelect)field.widget).setSelected(act.getCatalogList(field.name));
				break;
			case FT_OPTION_SELECTION:
			case FT_LIST_SELECTION:
				Catalog catalog = act.getCatalog(field.name);
				if (catalog != null) {
					((IEntitySelector<Catalog>)field.widget).setSelected(catalog);
				}
				break;
			case FT_HUMANBEING:
				Long id = act.getLong(field.name);
				if (id != null) {
					SRV.humanBeingService.findById(clDoc.getSession(), id, new DefaultCallback<Person>(clDoc, "findById") {

						@Override
						public void onSuccess(Person result) {
							((IEntitySelector<Person>)field.widget).setSelected(result);
						}
					});
					
				}
				break;
			case FT_DATE:
				Date value = act.getDate(field.name);
				if (value != null) {
					((DateTextBox) field.widget).setDate(value);
				}
				break;
			case FT_FLOAT:
				((FloatTextBox) field.widget).setFloat(act.getFloat(field.name));
				break;
//			case FT_TIME:
//				Date dateValue = act.getDate(field.name);
//				if (dateValue != null) {
//					((TimeTextBox) field.widget).setTime(dateValue);
//				}
//				break;
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void fromDialog() {
		for (Form<T> page:pages) {
			page.fromDialog();
		}
		
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			Field field = iter.next().getValue();
			String qualifiedFieldName = field.name;
			IAct act = model;// getAct(model, field);

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
			case FT_HUMANBEING:
				Person humanBeing = ((IEntitySelector<Person>) field.widget).getSelected(); 
				act.set(qualifiedFieldName, humanBeing != null ? humanBeing.id : null);
				break;
			case FT_DATE:
				act.set(qualifiedFieldName,
						((DateTextBox) field.widget).getDate());
				break;
			case FT_FLOAT:
				act.set(qualifiedFieldName,
						((FloatTextBox) field.widget).getFloat());
				break;
//			case FT_TIME:
//				act.set(qualifiedFieldName,
//						((TimeTextBox) field.widget).getDate());
//				break;
			}
		}

	}

	private PopupPanel popup;
	private boolean isModified = false;
	private int row;

	private final KeyDownHandler modificationHandler = new KeyDownHandler() {

		@Override
		public void onKeyDown(KeyDownEvent event) {
			onModification();
			if (!isModified) {
				isModified = true;
				if (setModified != null) {
					setModified.run();
				}
			}
		}
	};

	protected void onModification() {}
	
	@Override
	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public void clearModification() {
		isModified = false;
	}

	protected abstract void setup();

	private HorizontalPanel addButtons(final OnClick<T> onClickSave,
			final OnClick<T> onClickDelete, final OnClick<T> onClickCancel) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		if (onClickSave != null) {
			final Button pbOk = new Button(SRV.c.save());
			hp.add(pbOk);
//			pbOk.setStylePrimaryName("button");
//			pbOk.addStyleName("gray");
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
			final Button pbCancel = new Button(SRV.c.cancel());
			hp.add(pbCancel);
//			pbCancel.setStylePrimaryName("button");
//			pbCancel.addStyleName("gray");
			pbCancel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickCancel.onClick(model);
					popup.hide();
				}
			});
		}

		if (onClickDelete != null) {
			final Button pbCancel = new Button(SRV.c.delete());
			hp.add(pbCancel);
//			pbCancel.setStylePrimaryName("button");
//			pbCancel.addStyleName("gray");
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
		setWidget(row, 0, l);
		getCellFormatter().addStyleName(row, 0, "formLabel");		
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
			w.setWidth("60%");
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
			CatalogListBox clb = new CatalogListBox(clDoc, attributes.get("parent"));
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
			w.setWidth("60%");
			break;
		case FT_OPTION_SELECTION:
			CatalogRadioGroup crg = new CatalogRadioGroup(clDoc, attributes.get("parent"), attributes.get("orientation"));
			crg.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = crg;
			break;
		case FT_MULTI_SELECTION:
			CatalogMultiSelect cms = new CatalogMultiSelect(clDoc, attributes.get("parent"), attributes.get("orientation"));
			cms.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = cms;
			break;
		case FT_HUMANBEING:
			HumanBeingListBox hlb = new HumanBeingListBox(clDoc, attributes.get("role"), new OnDemandChangeListener<Person>() {
				
				@Override
				public void onChange(Person oldValue, Person newValue) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = hlb;
			w.setWidth("60%");
			break;
		case FT_TEXT:
			TextArea a = new TextArea();
			a.setWidth("99%");
			a.addKeyDownHandler(modificationHandler);
			w = a;
			break;
		case FT_DATE:
			DateTextBox d = new DateTextBox();
			d.setWidth("10em");
			d.addKeyDownHandler(modificationHandler);
			w = d;
			break;
		case FT_FLOAT:
			FloatTextBox f = new FloatTextBox();
			f.setWidth("5em");
			f.addKeyDownHandler(modificationHandler);
			w = f;
			break;
//		case FT_TIME:
//			TimeTextBox tbx = new TimeTextBox();
//			tbx.setWidth("4em");
//			tbx.addKeyDownHandler(modificationHandler);
//			w = tbx;
//			break;
		default:
			w = new TextBox();
			break;

		}
		return w;
	}

	protected void parseAndCreate(String xml) {
		parseAndCreate(xml, true);
	}
	
	protected void parseAndCreate(String xml, final boolean clear) {
		SRV.configurationService.parse(clDoc.getSession(), xml, new DefaultCallback<LayoutElement>(clDoc, "parse") {

			@Override
			public void onSuccess(LayoutElement result) {
				if (result.getChildren() != null && !result.getChildren().isEmpty()) {
					if (clear) {
						clear();
					}
					createAndLayout(result.getChildren().get(0));
					toDialog();
				}
			}
		});
	}


	protected void createAndLayout(LayoutElement layoutElement) {
		if (layoutElement.getType().equals("pages")) {
			TabLayoutPanel pageContainer = getPageContainer();
			for (LayoutElement child : layoutElement.getChildren()) {
				Form page = new Form<T>(clDoc, model, new Runnable() {
					
					@Override
					public void run() {
						if (!isModified) {
							setModified.run();
							isModified = true;
						}
					}
				})
				{

					@Override
					protected void setup() {
						setWidth("100%");
					}

				};
				pageContainer.add(page, child.getAttribute("label"));
				page.createAndLayout(child);
				pages.add(page);
			} 
		} else {
			ArrayList<LayoutElement> children = layoutElement.getChildren();
			for (LayoutElement child : children) {
				if (child.getType().equals("form")) {
					
				} else if (child.getType().equals("line")) {
					String label = child.getAttribute("label");
					String fieldName = child.getAttribute("name");
					
					if (child.getChildren().isEmpty()) {
						addLine(label == null ? fieldName : label, fieldName, getDataType(child), child.getAttributes());
					} else {
						HorizontalPanel hp = new HorizontalPanel();
						hp.setSpacing(5);
						hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
						for (LayoutElement sub:child.getChildren()) {
							DataTypes dataType = getDataType(sub);
							String subLabel = sub.getAttribute("label");
							String subName = sub.getAttribute("name");
							Label l = new Label(subLabel == null ? subName : subLabel);
							l.addStyleName("formSubLabel");
							Widget w = createWidgetForType(dataType, sub.getAttributes());
							hp.add(l);
							hp.add(w);
							fields.put(subName, new Field(subName, w, dataType));
						}
						addLine(label == null ? fieldName : label, hp);
					}
				}
			}
		}		
	}


	private TabLayoutPanel getPageContainer() {
		TabLayoutPanel pageContainer = (TabLayoutPanel)(getRowCount() == 1 ? getWidget(0, 0) : null);
		if (pageContainer == null) {
			pageContainer = new TabLayoutPanel(2, Unit.EM);
			pageContainer.setSize("800px", "600px");
			setWidget(0,  0, pageContainer);
		}
		return pageContainer;
	}

	private DataTypes getDataType(LayoutElement child) {
		String type = child.getAttribute("type");
		DataTypes result = DataTypes.FT_UNDEF;
		
		if (type != null) {
			type = type.toLowerCase();
	
			if (type.equals("string")) {
				result = DataTypes.FT_STRING;
			} else if (type.equals("text")) {
				result = DataTypes.FT_TEXT;
			} else if (type.equals("date")) {
				result = DataTypes.FT_DATE;
			} else if (type.equals("float")) {
				result = DataTypes.FT_FLOAT;
//				} else if (type.equals("time")) {
//				result = DataTypes.FT_TIME;
			} else if (type.equals("boolean")) {
				result = DataTypes.FT_BOOLEAN;
			} else if (type.equals("list")) {
				result = DataTypes.FT_LIST_SELECTION;
			} else if (type.equals("option")) {
				result = DataTypes.FT_OPTION_SELECTION;
			} else if (type.equals("multiselect")) {
				result = DataTypes.FT_MULTI_SELECTION;
			} else if (type.equals("humanbeing")) {
				result = DataTypes.FT_HUMANBEING;
			}
		}		
		
		return result;
	}

	@Override
	public T getModel() {
		return model;
	}	

}

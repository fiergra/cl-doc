package com.ceres.cldoc.client.views;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class TimeRegistration extends DockLayoutPanel {

	private static final String WORKINGTIME_ACT = "WorkingTime";
	private final ClDoc clDoc;
	private VerticalPanel itemListPanel;
	private DateBox dpDate;
	
	public TimeRegistration(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup(clDoc);
	}
	
	private void setup(ClDoc clDoc) {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);
		addNorth(header, 3);

		Label lbPrev = new Label("<");
		Label lbNext = new Label(">");
		lbPrev.setStyleName("participationDateButton");
		lbNext.setStyleName("participationDateButton");

		dpDate = new DateBox();
		dpDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
		dpDate.setValue(new Date());
		dpDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				selectActs(event.getValue());
			}
		});
		
		buttons.add(lbPrev);
		buttons.add(dpDate);
		buttons.add(lbNext);
		
		lbPrev.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incrementDate(-ONE_DAY);
			}
		});

		lbNext.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incrementDate(ONE_DAY);
			}
		});
		
		itemListPanel = new VerticalPanel();
		ScrollPanel sp = new ScrollPanel(itemListPanel);
		add(sp);
	}

	private IsWidget getWorkingtimeActRenderer(final Act entry) {
		final HorizontalPanel hp = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		final LinkButton lbSave = new LinkButton("Speichern", "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", null);
		lbSave.enable(false);
		buttons.add(lbSave);
		final IForm ar = ActRenderer.getActRenderer(clDoc, "<form><line name=\"asdf\" type=\"string\"/></form>", entry, new Runnable() {
			
			@Override
			public void run() {
				lbSave.enable(true);
			}
		}, null);

		ClickHandler chSave = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ar.fromDialog();
				saveItem(entry);
			}
		};
		lbSave.addClickHandler(chSave);
		LinkButton lbDelete = new LinkButton("Eintrag entfernen", "icons/32/File-Delete-icon.png", "icons/32/File-Delete-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (entry.id != null) {
					deleteItem(entry);
				} else {
					itemListPanel.remove(hp);
				}
			}
		});
		buttons.add(lbDelete);
		hp.add(ar);
		ar.toDialog();
		hp.add(buttons);
		
		return hp;
	}
	
	protected void saveItem(Act entry) {
		SRV.actService.save(clDoc.getSession(), entry, new DefaultCallback<Act>(clDoc, "save work sheet entry") {

			@Override
			public void onSuccess(Act act) {
				selectActs();
			}
		});
	}

	protected void deleteItem(final Act entry) {
		new MessageBox("Loeschen bestaetigen", "Soll der Eintrag entfernt werden?", MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION){
			@Override
			protected void onClick(int result) {
				if (result == MessageBox.MB_YES) {
					SRV.actService.delete(clDoc.getSession(), entry, new DefaultCallback<Void>(clDoc, "delete work sheet entry") {

						@Override
						public void onSuccess(Void result) {
							selectActs();
						}
					});
				}
			}

		}.show();
	}

	protected void addItem() {
		Act entry = new Act(new ActClass(WORKINGTIME_ACT));
		entry.date = dpDate.getValue(); 
		entry.setParticipant(clDoc.getSession().getUser().person, Participation.ADMINISTRATOR, entry.date, entry.date);
		IsWidget ar = getWorkingtimeActRenderer(entry);
//		ar.setModel(entry);
		itemListPanel.insert(ar, 0);
		
	}

	public static final long ONE_DAY = 1000l * 60l * 60l * 24l;
	public static final long ONE_WEEK = ONE_DAY * 7L;
	
	private void incrementDate(long offset) {
		Date date  = incrementDate(dpDate.getValue(), offset);
		dpDate.setValue(date);
		selectActs(date);
	}

	private void selectActs() {
		selectActs(dpDate.getValue());
	}
	
	private void selectActs(Date date) {
		SRV.actService.findByEntity(clDoc.getSession(), clDoc.getSession().getUser().person, Participation.ADMINISTRATOR.id, date, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

			@Override
			public void onSuccess(List<Act> result) {
				itemListPanel.clear();
				for (Act act:result) {
//					if (act.actClass.name.equals(WORKINGTIME_ACT)) {
						itemListPanel.add(getWorkingtimeActRenderer(act));
//					}
				}
				
				LinkButton lbAdd = new LinkButton("neuen Eintrag hinzufuegen", "icons/32/File-New-icon.png", "icons/32/File-New-icon.png", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						addItem();
					}
				});
				itemListPanel.add(lbAdd);

				
			}
		});
	}

	private Date incrementDate(Date date, long offset) {
		if (date == null) {
			date = new Date();
		}
		
		Date newDate = new Date(date.getTime() + offset);
		return newDate;
	}


}

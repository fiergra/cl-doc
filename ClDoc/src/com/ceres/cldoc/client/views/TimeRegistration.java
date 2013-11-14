package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.ActListRenderer.FieldDef;
import com.ceres.cldoc.client.views.Form.DataType;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class TimeRegistration extends DockLayoutPanel {

	private static final String WORKINGTIME_ACT = "WorkingTime";
	private final ClDoc clDoc;
	
	private ActListRenderer actListRenderer;
	private TimeSheetSummary summaryPanel;
	private DateBox dpDate;
	private LinkButton pbSave;
	private boolean isModified;


	public TimeRegistration(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup(clDoc);
	}
	
	private void setup(final ClDoc clDoc) {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);
		addNorth(header, 3);
		
		summaryPanel = new TimeSheetSummary(clDoc);
		addEast(summaryPanel, 15);

		Image lbPrev = new Image("/icons/16/arrow-mini-left-icon.png");
		Image lbNext = new Image("/icons/16/arrow-mini-right-icon.png");
		lbPrev.setStyleName("linkButton");
		lbNext.setStyleName("linkButton");

		dpDate = new DateBox();
		dpDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
		dpDate.setValue(new Date());
		dpDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				selectActs();
			}
		});
		dpDate.setStyleName("timeRegistrationDate");
		buttons.add(lbPrev);
		buttons.add(dpDate);
		buttons.add(lbNext);
		
		final ListBox cmbPrintOuts = new ListBox();
		cmbPrintOuts.setVisibleItemCount(1);
		Image lbPdf = new Image("/icons/16/Adobe-PDF-Document-icon.png");
		lbPdf.setStyleName("linkButton");
		lbPdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=timesheet&userid=" + clDoc.getSession().getUser().getId() + "&month=" + cmbPrintOuts.getValue(cmbPrintOuts.getSelectedIndex()) , "_blank", "");
			}
		});
		
		populatePrintOutBox(cmbPrintOuts); 
		buttons.add(cmbPrintOuts);
		buttons.add(lbPdf);
		
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

		final ActClass actClass = new ActClass(WORKINGTIME_ACT);
		List<FieldDef> types = new ArrayList<FieldDef>();
		HashMap<String, String> attribs = new HashMap<String, String>();
		attribs.put("role", Participation.ADMINISTRATOR.code);
		attribs.put("which", "start");
		types.add(new FieldDef("Von", DataType.FT_PARTICIPATION_TIME, true, attribs ));
		HashMap<String, String> attribsEnd = new HashMap<String, String>();
		attribsEnd.put("role", Participation.ADMINISTRATOR.code);
		attribsEnd.put("which", "end");
		types.add(new FieldDef("bis", DataType.FT_PARTICIPATION_TIME, true, attribsEnd));
		types.add(new FieldDef("Bemerkung", DataType.FT_STRING, false, null));

		LayoutPanel vp = new LayoutPanel();
		actListRenderer = new ActListRenderer(clDoc, types, null, new Runnable() {
			@Override
			public void run() {
				setModified(true);
			}
		}){

			@Override
			Act newAct() {
				Act newAct = new Act(actClass);
				Date date = dpDate.getValue();
				newAct.date = date; 
				newAct.setParticipant(clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR, date, date);

				return newAct;
			}
			
			@Override
			boolean isValid(List<ClInteractor>interactors, ClInteractor interactor) {
//				if (!overlaps(act) && normalWorkingHours(act)) {
//					return true;
//				} else {
//					new MessageBox("Check", "Is it correct?", MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION).show();
//					return false;
//				}
				return true;
			}};
			
		vp.add(actListRenderer);
		vp.setWidgetLeftWidth(actListRenderer, 0, Unit.PX, 100, Unit.PCT);
		vp.setWidgetTopHeight(actListRenderer, 0, Unit.PX, 100, Unit.PCT);
		pbSave = new LinkButton(SRV.c.save(), "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveAll(new Runnable() {
					
					@Override
					public void run() {
						setModified(false);
					}
				});
			}
		});
		pbSave.enable(false);
		vp.add(pbSave);
		vp.setWidgetLeftWidth(pbSave, 0, Unit.PX, 42, Unit.PX);
		vp.setWidgetBottomHeight(pbSave, 0, Unit.PX, 42, Unit.PX);
		add(vp);
		vp.ensureDebugId("vp");
		selectActs();
	}

	private void populatePrintOutBox(final ListBox cmbPrintOuts) {
		SRV.actService.findByEntity(clDoc.getSession(), clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, new DefaultCallback<List<Act>>(clDoc, "load") {

			@Override
			public void onSuccess(List<Act> result) {
				cmbPrintOuts.clear();
				if (result != null) {
					Set<Date> months = new TreeSet<Date>();
					for (Act act:result) {
						if (WORKINGTIME_ACT.equals(act.actClass.name)) {
							Participation p = act.getParticipation(Participation.ADMINISTRATOR);
							months.add(getFirstOfMonth(p.start));
						}
					}
					if (!months.isEmpty()) {
						DateTimeFormat dtf = DateTimeFormat.getFormat("MMM yy");
						for (Date m:months) {
							cmbPrintOuts.addItem(dtf.format(m), dtf.format(m));
						}
					}
				}
			}
		});
	}

	protected Date getFirstOfMonth(Date start) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMyyyy");
		DateTimeFormat dtf2 = DateTimeFormat.getFormat("dMMyyyy");
		return dtf2.parse("1" + dtf.format(start));
	}

	protected boolean normalWorkingHours(Act act) {
		return true;
	}

//	protected boolean overlaps(Act act) {
//	}

	private boolean overlap(Act a, Act act) {
		Participation p1 = a.getParticipation(Participation.ADMINISTRATOR);
		Participation p2 = act.getParticipation(Participation.ADMINISTRATOR);
		
		boolean overlap = p2.start.getTime() >= p1.start.getTime() && p2.start.getTime() <= p1.end.getTime();
		overlap |= p2.end.getTime() >= p1.start.getTime() && p2.end.getTime() <= p1.end.getTime();
		overlap |= p2.start.getTime() <= p1.start.getTime() && p2.end.getTime() >= p1.end.getTime();
		return overlap;
	}

	protected void setModified(boolean isModified) {
		if (isModified) {
			this.isModified = true;
		} else {
			this.isModified = false;
		}
		pbSave.enable(this.isModified);
	}


	public static final long ONE_DAY = 1000l * 60l * 60l * 24l;
	public static final long ONE_WEEK = ONE_DAY * 7L;
	
	protected void saveAll(final Runnable afterSave) {
		final List<Act>acts = actListRenderer.getModifiedActs();
			
		AsyncCallback<List<Act>> callback = new DefaultCallback<List<Act>>(){
			
			@Override
			public void onSuccess(List<Act> result) {
				actListRenderer.clearModification();
				if (afterSave != null) {
					afterSave.run();
				}
			}
		};

		if (!acts.isEmpty()) {
			SRV.actService.save(clDoc.getSession(), acts, callback );
		}
	}

	
	
	private void incrementDate(final long offset) {
		if (actListRenderer.isModified()) {
			new MessageBox("Aenderungen speichern", "Wollen Sie die Aenderungen speichern?", MessageBox.MB_YESNOCANCEL, MESSAGE_ICONS.MB_ICON_QUESTION){

				@Override
				protected void onClick(int result) {
					switch (result) {
					case MessageBox.MB_CANCEL: break;
					case MessageBox.MB_YES: saveAll(new Runnable() {
						
						@Override
						public void run() {
							Date date  = incrementDate(dpDate.getValue(), offset);
							dpDate.setValue(date);
							selectActs();
						}
					}); break;
					case MessageBox.MB_NO: actListRenderer.clearModification(); incrementDate(offset); break;
					}
					super.onClick(result);
				}
				
			}.show();
		} else {
			Date date  = incrementDate(dpDate.getValue(), offset);
			dpDate.setValue(date);
			selectActs();
		}		
	}


	private void selectActs() {
		selectActs(dpDate.getValue());
		summaryPanel.setDate(dpDate.getValue());
	}
	
	private void selectActs(final Date date) {
		SRV.actService.findByEntity(clDoc.getSession(), clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, date, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

			@Override
			public void onSuccess(List<Act> result) {
				actListRenderer.setActs(result, dpDate.getValue());
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
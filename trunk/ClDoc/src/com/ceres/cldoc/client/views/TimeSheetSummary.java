package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class TimeSheetSummary extends DockLayoutPanel {

	private final Label lbMonth = new Label();
	private final FlexTable daysTable = new FlexTable();

	DateTimeFormat dtf = DateTimeFormat.getFormat("LLL yy");
	private String currentMonth = "";
	private final ClDoc clDoc;
	private Date date;
	private Runnable onClickDate; 
	
	public TimeSheetSummary (ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup();
	}
	
	public Date getDate() {
		return date;
	}
	
//	private void populatePrintOutBox(final ListBox cmbPrintOuts) {
//		SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "load") {
//
//			@Override
//			public void onSuccess(List<Act> result) {
//				cmbPrintOuts.clear();
//				if (result != null) {
//					Set<Date> months = new TreeSet<Date>();
//					for (Act act:result) {
//						if (TimeRegistration.WORKINGTIME_ACT.equals(act.actClass.name)) {
//							Participation p = act.getParticipation(Participation.ADMINISTRATOR);
//							months.add(getFirstOfMonth(p.start));
//						}
//					}
//					if (!months.isEmpty()) {
//						DateTimeFormat dtf = DateTimeFormat.getFormat("MMM yy");
//						for (Date m:months) {
//							cmbPrintOuts.addItem(dtf.format(m), dtf.format(m));
//						}
//					}
//				}
//			}
//		});
//	}


	protected Date getFirstOfMonth(Date start) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMyyyy");
		DateTimeFormat dtf2 = DateTimeFormat.getFormat("dMMyyyy");
		return dtf2.parse("1" + dtf.format(start));
	}


	private void setup() {
//		HorizontalPanel header = new HorizontalPanel();
//		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		header.setStylePrimaryName("buttonsPanel");
//
//		HorizontalPanel buttons = new HorizontalPanel();
//		buttons.setSpacing(5);
//		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		header.add(buttons);
//
//		header.add(lbMonth);
//		addNorth(header, 3);
		add(new ScrollPanel(daysTable));
		
		lbMonth.addStyleName("timeRegistrationDate");
		
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
		
		daysTable.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Cell cell = daysTable.getCellForEvent(event);
				date = new Date(date.getTime());
				date.setDate(cell.getRowIndex() + 1);
				onClickDate.run();
			}
		});
		
//		populatePrintOutBox(cmbPrintOuts); 
//		buttons.add(cmbPrintOuts);
//		buttons.add(lbPdf);
//		
//		PushButton pbAddHoliday = new PushButton("Urlaub...");
//		buttons.add(pbAddHoliday);
//		pbAddHoliday.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				addHolidays();
//			}
//		});
		

	}

//	protected void addHolidays() {
//		Interactor interactor = new Interactor();
//		Widget w = WidgetCreator.createWidget("<form><line name=\"von\" type=\"participationtime\" which=\"start\" role=\"ADMINISTRATOR\"/><line name=\"bis\" type=\"participationtime\" which=\"end\" role=\"ADMINISTRATOR\"/></form>", interactor);
//		PopupManager.showModal("Urlaub anlegen", w, new OnClick<PopupPanel>() {
//			
//			@Override
//			public void onClick(PopupPanel pp) {
//				pp.hide();
//			}
//		}, null);
//	}

	public void setDate(final Date date) {
		this.date = date;
//		if (!dtf.format(date).equals(currentMonth)) {
			SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

				@Override
				public void onSuccess(List<Act> result) {
					currentMonth = dtf.format(date);
					lbMonth.setText(dtf.format(date));
					daysTable.removeAllRows();
					calculate(date, result, getLeaves(result));
				}
			});

//		}
	}

	protected List<Act> getLeaves(List<Act> result) {
		List<Act> leaves = new ArrayList<Act>();
		for (Act a:result) {
			if (a.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT)) {
				leaves.add(a);
			}
		}
		return leaves;
	}

	private List<List<Participation>> groupByDay(Date date, List<Act> result) {
		List<List<Participation>> grouped = new ArrayList<List<Participation>>(31);
		for (int i = 0; i < 31; i++) {
			grouped.add(null);
		}
		for (Act act:result) {
			if (act.actClass.name.equals(TimeRegistration.WORKINGTIME_ACT)) {
				Participation par = act.getParticipation(Participation.ADMINISTRATOR);
				
				if (par != null && par.start != null && par.end != null && currentMonth.equals(dtf.format(par.start))) {
					List<Participation> participations = getParticipations(grouped, par);
					participations.add(par);
				}
			}
		}

		return grouped;
	}
	
	private List<Participation> getParticipations(List<List<Participation>> grouped, Participation par) {
		int dayOfMonth = par.start.getDate();
		List<Participation> participations = grouped.get(dayOfMonth - 1);
		if (participations == null) {
			participations = new ArrayList<Participation>();
			grouped.set(dayOfMonth - 1, participations);
		}
		return participations;
	}

	private void calculate(Date date, List<Act> result, List<Act> leaves) {
		DateTimeFormat dtfRowHeader = DateTimeFormat.getFormat("dd., E");
		List<List<Participation>> grouped = groupByDay(date, result);
		int month = date.getMonth();
		Date curDate = new Date(date.getTime());
		curDate.setDate(1);
		int row = 0;
		
		while (curDate.getMonth() == month) {
			if (LeaveRegistration.isLeave(leaves, curDate) != null) {
				daysTable.getRowFormatter().addStyleName(row, "leaveDay");
			} 
			Label dayLabel = new Label(dtfRowHeader.format(curDate));
			dayLabel.addStyleName("headerDate");
			if (isWeekEnd(curDate)) {
				dayLabel.addStyleName("weekendDate");
			}
			daysTable.setWidget(row, 0, dayLabel);
			daysTable.getRowFormatter().addStyleName(row, "timeSheetRow");
			List<Participation> participations = grouped.get(row);
			if (participations != null) {
				daysTable.setWidget(row, 1, new Label(String.valueOf(participations.size())));
				daysTable.setWidget(row, 2, new Label(String.valueOf(getDurationAsString(getDuration(participations)))));
				daysTable.getRowFormatter().addStyleName(row, "workingDate");
			} else {
				daysTable.setWidget(row, 1, new Label("---"));
				daysTable.setWidget(row, 2, new Label("---"));
			}

			CalendarUtil.addDaysToDate(curDate, 1);
			row++;
		}

//		List <Participation> participations = new ArrayList<Participation>();
//		for (Act act:result) {
//			Participation par = act.getParticipation(Participation.ADMINISTRATOR);
//			if (par != null && par.start != null && par.end != null && currentMonth.equals(dtf.format(par.start))) {
//				participations.add(par);
//			}
//		}
		
	}

	private boolean isWeekEnd(Date curDate) {
		int weekDay = curDate.getDay();
		return  weekDay == 0 || weekDay == 6;
	}

	private int getDuration(List<Participation> participations) {
		int duration = 0;
		for (Participation p:participations) {
			duration += TimeRegistration.getDuration(p);
		}
		return duration;
	}

	private String getDurationAsString(int duration) {
		int hours = duration / 60;
		int minutes = duration % 60;
		return hours + ":" + minutes;
	}

	public void setOnClickDate(Runnable onClickDate) {
		this.onClickDate = onClickDate;
	}

}

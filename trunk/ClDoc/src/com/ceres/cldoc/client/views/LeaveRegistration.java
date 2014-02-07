package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Participation;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.components.ObjectAsWidget;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;

public class LeaveRegistration extends DockLayoutPanel {

	final DatePicker dpFrom = new DatePicker();
	final DatePicker dpTo = new DatePicker();
	final FlexTable leaveTable = new FlexTable();
	
	final ActClass annualLeaveClass = new ActClass(TimeRegistration.ANNUAL_LEAVE_ACT);
	final ActClass sickLeaveClass = new ActClass(TimeRegistration.SICK_LEAVE_ACT);

	private final class FromToValueChangeHandler implements
			ValueChangeHandler<Date> {

		private final DatePicker dpFrom;
		private final DatePicker dpTo;

		private FromToValueChangeHandler(DatePicker dpFrom, DatePicker dpTo) {
			this.dpFrom = dpFrom;
			this.dpTo = dpTo;
		}

		@Override
		public void onValueChange(ValueChangeEvent<Date> event) {
			if (event.getValue() != null && dpFrom.isDateEnabled(event.getValue())) {
				Act leave = isLeave(event.getValue());
				if (leave != null) {
					removeLeave(leave);
				} else {
					
					if (start == null) {
						start = new Date(event.getValue().getTime());
					} else if (start.getTime() == event.getValue().getTime()) {
						dpFrom.removeStyleFromDates("leaveStart", start);
						dpTo.removeStyleFromDates("leaveStart", start);
						start = null;
						if (end != null) {
							dpFrom.removeStyleFromDates("leaveEnd", end);
							dpTo.removeStyleFromDates("leaveEnd", end);
							end = null;
						}
						disableBefore(dpFrom, newDate());
						disableBefore(dpTo, newDate());
					} else if (end == null) {
						end = new Date(event.getValue().getTime());
						if (end.getTime() < start.getTime()) {
							Date swap = start;
							start = end;
							end = swap;
						}
						
					} else if (end.getTime() == event.getValue().getTime()) {
						dpFrom.removeStyleFromDates("leaveEnd", end);
						dpTo.removeStyleFromDates("leaveEnd", end);
						end = null;
					}
					
					setStyles(dpFrom, start, end);
					setStyles(dpTo, start, end);
					updateAct();
				}
			}
			dpFrom.setValue(null);
		}
	}

	protected static final int PARTICIPATION_COL_INDEX = 10;
	private final ClDoc clDoc;
	
	private LinkButton pbSaveAnnualLeave;
	private LinkButton pbSaveSickLeave;
	
	private Act leaveAct;
	private final ArrayList<Act> leaves = new ArrayList<Act>();

	public LeaveRegistration(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		newAct(clDoc);
		setup(clDoc);
	}
	
	public void removeLeave(final Act act) {
		new MessageBox("Loeschen", "Wollen Sie den Urlaub entfernen?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

			@Override
			protected void onClick(int result) {
				if (result == MessageBox.MB_YES) {
					act.isDeleted = true;
					SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "delete") {

						@Override
						public void onSuccess(Act result) {
							leaves.remove(act);
							showLeaves();
						}
					});
				}
			}};
	}

	private Act isLeave(Date value) {
		return isLeave(leaves, value);
	}

	public static Act isLeave(List<Act> leaves, Date value) {
		Act leave = null;
		for (Act l:leaves) {
			Participation p = l.getParticipation(Participation.ADMINISTRATOR);
			if (contains(p, value)) {
				leave = l;
			}
		}
		
		return leave;
	}

	private static boolean contains(Participation p, Date value) {
		return p.start.getTime() <= value.getTime() && p.end.getTime() >= value.getTime();
	}

	public void updateAct() {
		Participation p = leaveAct.getParticipation(Participation.ADMINISTRATOR);
		p.start = start;
		p.end = end;
		
		String text = "";
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
		if (start == null) {
			text = "";
			pbSaveAnnualLeave.enable(false);
			pbSaveSickLeave.enable(false);
		} else {
			if (end == null) {
				text = "Abwesenheit am " + dtf.format(start); 
			} else {
				text = "Abwesenheit von " + dtf.format(start) + " bis " + dtf.format(end); 
			}
			pbSaveAnnualLeave.enable(true);
			pbSaveSickLeave.enable(true);
		}
		label.setText(text);
	}

	public static String getDurationAsString(Participation par) {
		return par.start != null && par.end != null ? getDurationAsString(par.start, par.end) : "---";
	}

	public static String getDurationAsString(Date start, Date end) {
		int duration = getDuration(start, end);
		int hours = duration / 60;
		int minutes = duration % 60;
		return hours + ":" + minutes;
	}

	public static int getDuration(Participation p) {
		return getDuration(p.start, p.end);
	}

	public static int getDuration(Date start, Date end) {
		int duration = (int)(end.getTime() - start.getTime())/ (1000 * 60);
		return duration;
	}

	private Date start;
	private Date end;
	private Label label;
	
	private void setup(final ClDoc clDoc) {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);

		pbSaveAnnualLeave = new LinkButton("Urlaub", "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				save(annualLeaveClass);
			}
		});
		pbSaveAnnualLeave.enable(false);
		pbSaveAnnualLeave.setPixelSize(32, 32);

		pbSaveSickLeave = new LinkButton("Krankmeldung", "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				save(sickLeaveClass);
			}
		});
		pbSaveSickLeave.enable(false);
		pbSaveSickLeave.setPixelSize(32, 32);
		
		buttons.add(pbSaveAnnualLeave);
		buttons.add(pbSaveSickLeave);
		
		label = new Label();
		buttons.add(label);
		addNorth(header, 3);
		
		HorizontalPanel fromToPanel = new HorizontalPanel();
		fromToPanel.add(dpFrom);
		fromToPanel.add(dpTo);
		fromToPanel.add(leaveTable);
		leaveTable.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Cell cell = leaveTable.getCellForEvent(event);
				Participation p = ((ObjectAsWidget<Participation>)leaveTable.getWidget(cell.getRowIndex(), PARTICIPATION_COL_INDEX)).getObject();
				dpFrom.setCurrentMonth(p.start);
				Date nextMonth = new Date(dpFrom.getCurrentMonth().getTime());
				CalendarUtil.addMonthsToDate(nextMonth, 1);
				dpTo.setCurrentMonth(nextMonth);
			}
		});
		
		Date cm = new Date(dpFrom.getCurrentMonth().getTime());
		CalendarUtil.addMonthsToDate(cm, 1);
		dpTo.setCurrentMonth(cm);
		
		add(fromToPanel);

		dpFrom.addValueChangeHandler(new FromToValueChangeHandler(dpFrom, dpTo));
		dpTo.addValueChangeHandler(new FromToValueChangeHandler(dpTo, dpFrom));


		dpFrom.addShowRangeHandler(new ShowRangeHandler<Date>() {
			
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				disableBefore(dpFrom, newDate());
				if (dpFrom.getCurrentMonth().getTime() >= dpTo.getCurrentMonth().getTime()) {
					scrollRight(dpFrom, dpTo);
				}
				showLeaves();
			}
		});
		
		dpTo.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				disableBefore(dpTo, dpFrom.getValue() != null ? dpFrom.getValue() : new Date());
				if (dpTo.getCurrentMonth().getTime() <= dpFrom.getCurrentMonth().getTime()) {
					scrollLeft(dpFrom, dpTo);
				}
			}
		});
		

		selectActs();
	}

	private void newAct(final ClDoc clDoc) {
		leaveAct = new Act();
		leaveAct.setParticipant(clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR, start, end);
		start = null;
		end = null;
	}

	protected Date newDate() {
		Date today = new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		return today;
	}

	protected void setStyles(DatePicker dp, Date start, Date end) {
		if (start == null) {
			disableBefore(dp, newDate());
			removeLeaveDays(dp);
		} else {
			disableBefore(dp, start);
			if (end != null) {
				Date cur = new Date(start.getTime());
				while (cur.getTime() < end.getTime()) {
					if (dp.isDateVisible(cur)) {
						dp.addStyleToDates("leaveDay", cur);
					}
					CalendarUtil.addDaysToDate(cur, 1);
				}
			} else {
				removeLeaveDays(dp);
			}
		}
		
		if (start != null) {
			dp.addStyleToDates("leaveStart", start);
		}
		if (end != null) {
			dp.addStyleToDates("leaveEnd", end);
		}
	}

	private void removeLeaveDays(DatePicker dp) {
		Date cur = new Date(dp.getFirstDate().getTime());
		while (cur.getTime() < dp.getLastDate().getTime()) {
			if (dp.isDateVisible(cur)) {
				dp.removeStyleFromDates("leaveDay", cur);
				dp.removeStyleFromDates("sickLeaveDay", cur);
			}
			CalendarUtil.addDaysToDate(cur, 1);
		}
		
	}

	protected void scrollRight(DatePicker dpFrom, DatePicker dpTo) {
		Date cm = new Date(dpFrom.getCurrentMonth().getTime());
		CalendarUtil.addMonthsToDate(cm, 1);
		dpTo.setCurrentMonth(cm);
	}

	protected void scrollLeft(DatePicker dpFrom, DatePicker dpTo) {
		Date cm = new Date(dpTo.getCurrentMonth().getTime());
		CalendarUtil.addMonthsToDate(cm, -1);
		dpFrom.setCurrentMonth(cm);
	}

	protected void disableBefore(DatePicker datePicker, Date value) {
		if (value.getTime() >= datePicker.getLastDate().getTime()) {
			value = new Date(datePicker.getLastDate().getTime());
		}
		if (datePicker.isDateVisible(value)) {
			Date cur = new Date(datePicker.getFirstDate().getTime());
			while (cur.getTime() < value.getTime()) {
				datePicker.setTransientEnabledOnDates(false, cur);
				CalendarUtil.addDaysToDate(cur, 1);
			}
			while (cur.getTime() < datePicker.getLastDate().getTime()) {
				datePicker.setTransientEnabledOnDates(true, cur);
				CalendarUtil.addDaysToDate(cur, 1);
			}
		}
	}

	protected boolean overlaps(Interactor next, Interactor interactor) {
		boolean overlap = false;
		
		if (interactor.isValid() && interactor.isValid() && !next.isEmpty() && !interactor.isEmpty()) {
			Act act1 = new Act();
			act1.setParticipant(null, Participation.ADMINISTRATOR);
			Act act2 = new Act();
			act2.setParticipant(null, Participation.ADMINISTRATOR);
			
			interactor.fromDialog(act2);
			next.fromDialog(act1);
			
			Participation p1 = act1.getParticipation(Participation.ADMINISTRATOR);
			Participation p2 = act2.getParticipation(Participation.ADMINISTRATOR);
			
			if (p1.start != null && p1.end != null && p2.start != null && p2.end != null) {
				overlap = p2.start.getTime() > p1.start.getTime() && p2.start.getTime() < p1.end.getTime();
				overlap |= p2.end.getTime() > p1.start.getTime() && p2.end.getTime() < p1.end.getTime();
				overlap |= p2.start.getTime() < p1.start.getTime() && p2.end.getTime() > p1.end.getTime();
			}
		}
		return overlap;
	}

	protected Date getFirstOfMonth(Date start) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMyyyy");
		DateTimeFormat dtf2 = DateTimeFormat.getFormat("dMMyyyy");
		return dtf2.parse("1" + dtf.format(start));
	}

	protected boolean normalWorkingHours(Act act) {
		return true;
	}


	protected void save(ActClass actClass) {
		leaveAct.actClass = actClass;
		SRV.actService.save(clDoc.getSession(), leaveAct, new DefaultCallback<Act>(clDoc, "save leave") {

			@Override
			public void onSuccess(Act result) {
				start = null;
				end = null;
				leaves.add(result);
				showLeaves();
				newAct(clDoc);
				updateAct();
			}
		});
	}

	private void selectActs() {
		SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

			@Override
			public void onSuccess(List<Act> result) {
				leaves.clear();
				for (Act a:result) {
					if (isLeave(a)) {
						leaves.add(a);
					}
				}
//				Lists.sort(leaves, new Comparator<Act>() {
//
//					@Override
//					public int compare(Act o1, Act o2) {
//						Participation p1 = o1.getParticipation(Participation.ADMINISTRATOR);
//						Participation p2 = o2.getParticipation(Participation.ADMINISTRATOR);
//						
//						return p1.start.compareTo(p2.start);
//					}
//				});
				showLeaves();
			}
		});

	}

	protected boolean isLeave(Act a) {
		return a.actClass.equals(annualLeaveClass) || a.actClass.equals(sickLeaveClass); 
	}

	protected void showLeaves() {
		removeLeaveDays(dpFrom);
		removeLeaveDays(dpTo);
		
		leaveTable.clear();
		int i = 0;
		for (Act l:leaves) {
			Participation p = l.getParticipation(Participation.ADMINISTRATOR);
			showLeave(l.actClass.name, dpFrom, p);
			showLeave(l.actClass.name, dpTo, p);
			addLeave(i++, p);
		}
	}

	private void addLeave(int row, Participation leave) {
		int col = 0;
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.");
		Label l = new Label(dtf.format(leave.start));
		leaveTable.setWidget(row, col++, l);
		l = new Label(dtf.format(leave.end));
		leaveTable.setWidget(row, col++, l);
		leaveTable.setWidget(row, PARTICIPATION_COL_INDEX, new ObjectAsWidget<Participation>(leave));
		
		leaveTable.getRowFormatter().addStyleName(row, "timeSheetRow");
	}

	private void showLeave(String className, DatePicker dp, Participation p) {
		Date start = p.start;
		Date end = p.end;
		
		Date cur = new Date(start.getTime());
		while (cur.getTime() < end.getTime()) {
			if (dp.isDateVisible(cur)) {
				if (className.equals(TimeRegistration.ANNUAL_LEAVE_ACT)) {
					dp.addStyleToDates("leaveDay", cur);
				} else {
					dp.addStyleToDates("sickLeaveDay", cur);
				}
			}
			CalendarUtil.addDaysToDate(cur, 1);
		}

//		if (start != null) {
//			dp.addStyleToDates("leaveStart", start);
//		}
//		if (end != null) {
//			dp.addStyleToDates("leaveEnd", end);
//		}

	}


}

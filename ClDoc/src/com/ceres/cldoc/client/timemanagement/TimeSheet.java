package com.ceres.cldoc.client.timemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.ParticipationTimeFactory;
import com.ceres.cldoc.client.views.PopupManager;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.ActAsTimeSheetElement;
import com.ceres.cldoc.timemanagement.ITimeManagementService;
import com.ceres.cldoc.timemanagement.TimeSheetDay;
import com.ceres.cldoc.timemanagement.TimeSheetElement;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.dynamicforms.client.DateLink;
import com.ceres.dynamicforms.client.DurationLink;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.TextLink;
import com.ceres.dynamicforms.client.components.MapListRenderer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeSheet extends DockLayoutPanel {

	private final Date date = new Date();
	private final ClDoc clDoc;


	public TimeSheet(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		
		SRV.timeManagementService.loadTimeSheetYear(clDoc.getSession(), (Person) clDoc.getSession().getUser().getPerson(), new Date().getYear() + 1900, new DefaultCallback<TimeSheetYear>(clDoc, "load timesheet") {

			@Override
			public void onSuccess(TimeSheetYear tsy) {
				setup(tsy);
			}
		});
		
	}

	
	private void setup(TimeSheetYear tsy) {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);

		final Label lbYear = new Label(String.valueOf(tsy.getYear()));
		header.add(lbYear);
		
		addNorth(header, 3);

		Image lbPrev = new Image("/icons/16/arrow-mini-left-icon.png");
		Image lbNext = new Image("/icons/16/arrow-mini-right-icon.png");
		lbPrev.setStyleName("linkButton");
		lbNext.setStyleName("linkButton");

		buttons.add(lbPrev);
		buttons.add(lbYear);
		buttons.add(lbNext);

		lbPrev.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			}
		});

		lbNext.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		for (TimeSheetElement tsm:tsy.getChildren()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat("MMMM");
			VerticalPanel vp = new VerticalPanel();
			vp.add(new Label(dtf.format(tsm.getDate())));
			vp.add(createMonthTable(tsm));
			hp.add(vp);
		}

		add(new ScrollPanel(hp));
	}

	
	private void editDay(final TimeSheetDay tsd) {
		VerticalPanel vp = new VerticalPanel();
		final MapListRenderer actListRenderer = new MapListRenderer(new String[]{"von", "bis", "Bemerkung", "Dauer"}, 
				new Runnable() {
					@Override
					public void run() {
//						setModified(true);
					}
				}) {
			
			@Override
			protected Map<String, Serializable> newAct() {
				Act newAct = new Act(new ActClass(ITimeManagementService.WORKINGTIME_ACT));
				Date date = tsd.getDate();
				newAct.date = date; 
				newAct.setParticipant(clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR, date, date);

				return newAct;
			}
			
			
			@Override
			protected void createNewRow(final int row, final Interactor interactor) {
				int col = 0;
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes.put("role", Participation.ADMINISTRATOR.code);
				attributes.put("which", "start");
				attributes.put("required", "true");
				final DateLink fromLink = new ParticipationTimeFactory().createLink(interactor, "von", attributes );
				setWidget(row, col, fromLink.getWidget());
				interactor.addLink(fromLink);
				col++;
				attributes.put("which", "end");
				final DateLink toLink = new ParticipationTimeFactory().createLink(interactor, "bis", attributes );
				setWidget(row, col, toLink.getWidget());
				interactor.addLink(toLink);
				col++;

				TextBox textBox = new TextBox();
				setWidget(row, col, textBox);
				interactor.addLink(new TextLink(interactor, "Bemerkung", textBox, null));
				col++;

				col++;
				final Label lblDuration = new Label();
				setWidget(row, col, lblDuration);
				
				interactor.addLink(new DurationLink(interactor, fromLink, toLink) {

					@Override
					protected void hilite(boolean isValid) {
						lblDuration.setText("---");
						if (isValid) {
							getRowFormatter().removeStyleName(row, "invalidContent");
							if (!isEmpty()) {
//								lblDuration.setText(getDurationAsString(fromLink.getWidget().getDate(), toLink.getWidget().getDate()));
							}
						} else {
							getRowFormatter().addStyleName(row, "invalidContent");
						}
					}
					
				});
				
			}
			
			@Override
			protected boolean canRemove(final Map<String, Serializable> row) {
				Act act = (Act)row;
				
				if (act.id != null) {
					act.isDeleted = true;
					SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "delete act") {

						@Override
						public void onSuccess(Act result) {
//							actListRenderer.removeAct(row);
						}
					});
					return false;
				} else {
					return true;
				}
			}

			@Override
			protected boolean isValid(Interactor interactor) {
				return true;//!isOverlapping(interactor);
			}
		};
				
		vp.add(actListRenderer);
		actListRenderer.setActs(asMaps(tsd.getChildren()));
		
		PopupManager.showModal("Anwesenheit", vp, new OnClick<PopupPanel>() {
			
			@Override
			public void onClick(PopupPanel pp) {
				pp.hide();
				tsd.setActs(actListRenderer.getActs());
			}
		}, null);

	}

	private List<Map<String, Serializable>> asMaps(List<TimeSheetElement> children) {
		
		List<Map<String, Serializable>> maps = null;
		
		if (children != null) {
			maps = new ArrayList<Map<String,Serializable>>(children.size());
			for (TimeSheetElement ts:children) {
				ActAsTimeSheetElement aats = (ActAsTimeSheetElement) ts;
				maps.add(aats.getAct());
			}
		}
		
		return maps;
	}


	private Widget createMonthTable(final TimeSheetElement tsm) {
		final FlexTable ft = new FlexTable();
		ft.addStyleName("monthTable");
		ft.getColumnFormatter().addStyleName(0, "dateColumn");
		DateTimeFormat dtfRowHeader = DateTimeFormat.getFormat("dd., E");
		
		ft.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Cell cell = ft.getCellForEvent(event);
				int index = cell.getRowIndex();
				TimeSheetElement day = tsm.getChildren().get(index);
				editDay((TimeSheetDay) day);
			}
		});
		
		int row = 0;
		Date now = new Date();
		for (TimeSheetElement tse:tsm.getChildren()) {
			TimeSheetDay tsd = (TimeSheetDay)tse;

			HorizontalPanel hp = new HorizontalPanel();
			hp.addStyleName("timeSheetRow");
			Label dayLabel = new Label(dtfRowHeader.format(tse.getDate()));
			dayLabel.addStyleName("headerDate");

			hp.add(dayLabel);
			ft.setWidget(row, 0, hp);
			
			
			
			if (tse.isAbsent()) {
				if (tse.getAbsenceType().equals(TimeSheetElement.AbsenceType.HOLIDAY)) {
					ft.getRowFormatter().addStyleName(row, "leaveDay");
				} else {
					ft.getRowFormatter().addStyleName(row, "sickLeaveDay");
				}

			} 
			if (tsd.isHoliday()) {
				ft.getRowFormatter().addStyleName(row, "weekendCell");
				dayLabel.addStyleName("weekendDate");
			}
			
			hp.setTitle(getToolTip(tse));
			
			if (tse.getBalance() != 0) {
				Label lbBalance = new Label(String.valueOf(getDurationAsString(tse.getBalance())));
				hp.add(lbBalance);
				lbBalance.setStyleName(tse.getBalance() >=  0 ? "positiveBalance" : "negativeBalance"); 
			}			
			
//			ft.getRowFormatter().addStyleName(row, "timeSheetRow");
//			ft.getRowFormatter().addStyleName(row, "workingDate");
			
//			if (tsd.getDate().getTime() < now.getTime()) {
//				ft.setWidget(row, 2, new Label(String.valueOf(getDurationAsString(tse.getWorkingTime()))));
//				ft.setWidget(row, 3, new Label(String.valueOf(getDurationAsString(tse.getQuota()))));
//				ft.setWidget(row, 4, new Label(String.valueOf(getDurationAsString(tse.getBalance()))));
//			}
			row++;
		}
		
		return ft;
	}	
	
	private String getToolTip(TimeSheetElement tse) {
		String tt = 
				getDurationAsString(tse.getBalance()) + "[" +  
				getDurationAsString(tse.getWorkingTime()) + "/" + 
				getDurationAsString(tse.getQuota()) + "]";

		return tt;
	}


	private String getDurationAsString(int duration) {
		NumberFormat nf = NumberFormat.getFormat("00");
		
		duration = duration < 0 ? -1 * duration : duration;
		int hours = duration / 60;
		int minutes = duration % 60;
		
		return nf.format(hours) + ":" + nf.format(minutes);
//		return String.format("%01d:%01d", hours, minutes);
	}


	
//	private void setup() {
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
//		final Label lbPattern = new Label();
//		header.add(lbPattern);
//
//		final TimeSheetSummaryTable ts1 = new TimeSheetSummaryTable(clDoc);
//		final TimeSheetSummaryTable ts2 = new TimeSheetSummaryTable(clDoc);
//		final TimeSheetSummaryTable ts3 = new TimeSheetSummaryTable(clDoc);
//
//		SRV.timeManagementService.getWorkPattern(clDoc.getSession(), 
//				new DefaultCallback<WorkPattern>(clDoc, "getWorkPattern") {
//
//					@Override
//					public void onSuccess(WorkPattern wp) {
//						if (wp != null) {
//							lbPattern.setText(wp.weeklyHours + "h");
//							ts1.setWorkPattern(wp);
//							ts2.setWorkPattern(wp);
//							ts3.setWorkPattern(wp);
//							load(wp, ts1, ts2, ts3);
//						} else {
//							lbPattern.setText("kein Arbeitszeitmuster zugewiesen!");
//						}
//					}
//		});
//		addNorth(header, 3);
//
//		Image lbPrev = new Image("/icons/16/arrow-mini-left-icon.png");
//		Image lbNext = new Image("/icons/16/arrow-mini-right-icon.png");
//		lbPrev.setStyleName("linkButton");
//		lbNext.setStyleName("linkButton");
//		
//		buttons.add(lbPrev);
//		buttons.add(lbNext);
//		
//		lbPrev.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//			}
//		});
//
//		lbNext.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//			}
//		});
//		
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.add(ts1);
//		hp.add(ts2);
//		hp.add(ts3);
//		
//		
//		add(new ScrollPanel(hp));
//	}
//
//	protected List<Act> getLeaves(List<Act> result) {
//		List<Act> leaves = new ArrayList<Act>();
//		for (Act a:result) {
//			if (a.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT) || a.actClass.name.equals(TimeRegistration.SICK_LEAVE_ACT)) {
//				leaves.add(a);
//			}
//		}
//		return leaves;
//	}
//
//

//	protected void load(final WorkPattern wp, final TimeSheetSummaryTable ts1, final TimeSheetSummaryTable ts2, final TimeSheetSummaryTable ts3) {
//		SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {
//
//			@Override
//			public void onSuccess(List<Act> result) {
//				List<Act> leaves = getLeaves(result);
//				TimeSheetModel tsm3 = new TimeSheetModel(wp, date, 0f);
//				tsm3.calculate(result, leaves);
//				ts3.setModel(tsm3);
//				Date date2 = new Date();
//				Date date3 = new Date();
//				CalendarUtil.addMonthsToDate(date2, -1);
//				TimeSheetModel tsm2 = new TimeSheetModel(wp, date2, 0f);
//				tsm2.calculate(result, leaves);
//				ts2.setModel(tsm2);
//				CalendarUtil.addMonthsToDate(date3, -2);
//				TimeSheetModel tsm1 = new TimeSheetModel(wp, date3, 0f);
//				tsm1.calculate(result, leaves);
//				ts1.setModel(tsm1);
//			}
//		});
//
//	}
//

}

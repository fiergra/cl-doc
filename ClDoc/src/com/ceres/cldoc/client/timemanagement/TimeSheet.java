package com.ceres.cldoc.client.timemanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class TimeSheet extends DockLayoutPanel {

	private final Date date = new Date();
	private final ClDoc clDoc;


	public TimeSheet(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup();
	}

	protected List<Act> getLeaves(List<Act> result) {
		List<Act> leaves = new ArrayList<Act>();
		for (Act a:result) {
			if (a.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT) || a.actClass.name.equals(TimeRegistration.SICK_LEAVE_ACT)) {
				leaves.add(a);
			}
		}
		return leaves;
	}


	private void setup() {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);

		final Label lbPattern = new Label();
		header.add(lbPattern);

		final TimeSheetSummaryTable ts1 = new TimeSheetSummaryTable(clDoc);
		final TimeSheetSummaryTable ts2 = new TimeSheetSummaryTable(clDoc);
		final TimeSheetSummaryTable ts3 = new TimeSheetSummaryTable(clDoc);

		SRV.timeManagementService.getWorkPattern(clDoc.getSession(), 
				new DefaultCallback<WorkPattern>(clDoc, "getWorkPattern") {

					@Override
					public void onSuccess(WorkPattern wp) {
						if (wp != null) {
							lbPattern.setText(wp.weeklyHours + "h");
							ts1.setWorkPattern(wp);
							ts2.setWorkPattern(wp);
							ts3.setWorkPattern(wp);
							load(wp, ts1, ts2, ts3);
						} else {
							lbPattern.setText("kein Arbeitszeitmuster zugewiesen!");
						}
					}
		});
		addNorth(header, 3);

		Image lbPrev = new Image("/icons/16/arrow-mini-left-icon.png");
		Image lbNext = new Image("/icons/16/arrow-mini-right-icon.png");
		lbPrev.setStyleName("linkButton");
		lbNext.setStyleName("linkButton");
		
		buttons.add(lbPrev);
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
		hp.add(ts1);
		hp.add(ts2);
		hp.add(ts3);
		
		
		add(new ScrollPanel(hp));
	}

	protected void load(final WorkPattern wp, final TimeSheetSummaryTable ts1, final TimeSheetSummaryTable ts2, final TimeSheetSummaryTable ts3) {
		SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

			@Override
			public void onSuccess(List<Act> result) {
				List<Act> leaves = getLeaves(result);
				TimeSheetModel tsm3 = new TimeSheetModel(wp, date, 0f);
				tsm3.calculate(result, leaves);
				ts3.setModel(tsm3);
				Date date2 = new Date();
				Date date3 = new Date();
				CalendarUtil.addMonthsToDate(date2, -1);
				TimeSheetModel tsm2 = new TimeSheetModel(wp, date2, 0f);
				tsm2.calculate(result, leaves);
				ts2.setModel(tsm2);
				CalendarUtil.addMonthsToDate(date3, -2);
				TimeSheetModel tsm1 = new TimeSheetModel(wp, date3, 0f);
				tsm1.calculate(result, leaves);
				ts1.setModel(tsm1);
			}
		});

	}


}

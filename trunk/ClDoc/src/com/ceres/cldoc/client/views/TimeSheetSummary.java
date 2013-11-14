package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class TimeSheetSummary extends DockLayoutPanel {

	private final Label lbMonth = new Label();
	private final FlexTable table = new FlexTable();

	DateTimeFormat dtf = DateTimeFormat.getFormat("LLL yy");
	private String currentMonth = "";
	private final ClDoc clDoc; 
	
	public TimeSheetSummary (ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup();
	}
	
	private void setup() {
		addNorth(lbMonth, 3);
		add(table);
		
		lbMonth.addStyleName("timeRegistrationDate");
	}

	public void setDate(final Date date) {
		if (!dtf.format(date).equals(currentMonth)) {
			SRV.actService.findByEntity(clDoc.getSession(), clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

				@Override
				public void onSuccess(List<Act> result) {
					currentMonth = dtf.format(date);
					lbMonth.setText(dtf.format(date));
					table.removeAllRows();
					calculate(date, result);
				}
			});

		}
	}

	private void calculate(Date date, List<Act> result) {
		int row = 0;
		List <Participation> participations = new ArrayList<Participation>();
		for (Act act:result) {
			Participation par = act.getParticipation(Participation.ADMINISTRATOR);
			if (par != null && par.start != null && par.end != null && currentMonth.equals(dtf.format(par.start))) {
				participations.add(par);
			}
		}
		
		Collections.sort(participations, new Comparator<Participation>(){

			@Override
			public int compare(Participation o1, Participation o2) {
				return o1.start.compareTo(o2.start);
			}});
		DateTimeFormat dayFormat = DateTimeFormat.getFormat("EEE dd");
		for (Participation par:participations) {
			
			table.setWidget(row, 0, new Label(dayFormat.format(par.start)));
			table.setWidget(row, 1, new Label(getDuration(par)));
			row++;
			
		}
	}

	private String getDuration(Participation par) {
		int duration = (int)(par.end.getTime() - par.start.getTime())/ (1000 * 60);
		int hours = duration / 60;
		int minutes = duration % 60;
		return hours + ":" + minutes;
	}

}

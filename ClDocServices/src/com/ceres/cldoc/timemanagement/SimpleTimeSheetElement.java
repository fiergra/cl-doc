package com.ceres.cldoc.timemanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

public class SimpleTimeSheetElement implements TimeSheetElement {
	private static final long serialVersionUID = -2991289817578845216L;
	
	private Date date;
	private int workingTime;
	private Act absence = null;
	
	private List<TimeSheetElement> children;
	private TimeSheetElement parent;

	public SimpleTimeSheetElement() {
	}

	public SimpleTimeSheetElement(TimeSheetElement parent) {
		this.parent = parent;
	}
	
	public SimpleTimeSheetElement(TimeSheetElement parent, Date date) {
		this(parent);
		this.date = date;
	}

	
	public TimeSheetElement getParent() {
		return parent;
	}

	protected void clearChildren() {
		children = null;
	}


	@Override
	public int getQuota() {
		int q;

		q = 0;
		if (hasChildren()) {
			for (TimeSheetElement tse:getChildren()) {
				q += tse.getQuota();
			}
		}

		return q;
	}

	@Override
	public boolean isAbsent() {
		return absence != null;
	}

	@Override
	public int getWorkingTime() {
		if (hasChildren()) {
			workingTime = 0;
			for (TimeSheetElement tse:getChildren()) {
				workingTime += tse.getWorkingTime();
			}
		}
		return workingTime;
	}


	@Override
	public List<TimeSheetElement> getChildren() {
		return children;
	}

	@Override
	public void addChild(TimeSheetElement element) {
		if (children == null) {
			children = new ArrayList<TimeSheetElement>();
		}
		children.add(element);
	}

	@Override
	public boolean hasChildren() {
		return getChildren() != null && !getChildren().isEmpty();
	}

	@Override
	public int getBalance() {
		return isAbsent() ? 0 : getWorkingTime() - getQuota();
	}

	public void setWorkingTime(int workingTime) {
		this.workingTime = workingTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("SimpleTimeSheetElement [absent=" + getAnnualLeaveDays() + ", quota=" + getQuota() + ", workingTime="
				+ getWorkingTime() + "(" + getBalance() + ")]\n");
		if (hasChildren()) {
			for (TimeSheetElement tse:getChildren()) {
				sb.append(tse.toString());  
			}
		}

		return sb.toString();
	}

	
	public boolean isAnnualLeave() {
		return getAbsence() != null ? getAbsence().actClass.name.equals(ITimeManagementService.ANNUAL_LEAVE_ACT) : false;
	}

	
//	private boolean isPublicHoliday() {
//		return isAbsent() && isAnnualLeave(getAbsence());
//	}

	
	@Override
	public float getAnnualLeaveDays() {
		float absences = 0f;
		
		if (isAbsent() && isAnnualLeave()) {
			absences = checkHalfDays(getAbsence().getParticipation(Participation.ADMINISTRATOR), getDate());
		} else {
			if (hasChildren()) {
				for (TimeSheetElement tse:getChildren()) {
					absences += tse.getAnnualLeaveDays();  
				}

			}
		}
		return absences;
	}

	
	private Integer ldate(Date d) {
		return (d.getYear() + 1900) * 10000 + d.getMonth() * 100 + d.getDate();
	}

	public float getAbsenceDays() {
		return isAbsent() ? checkHalfDays(getAbsence().getParticipation(Participation.ADMINISTRATOR), getDate()) : 0;  
	}

	private float checkHalfDays(Participation participation, Date date) {
		long start = ldate(participation.start);
		long end = ldate(participation.end);
		long ldate = ldate(date);
		float leaveDays = 1f;
		
		if (ldate == start && ldate == end) {
			leaveDays = (getAbsence().getBoolean(ITimeManagementService.HALFDAY_START) || getAbsence().getBoolean(ITimeManagementService.HALFDAY_END)) ? 0.5f : 1f;
		} else if (ldate == start) {
			leaveDays = getAbsence().getBoolean(ITimeManagementService.HALFDAY_START) ? 0.5f : 1f;
		} else if (ldate == end) {
			leaveDays = getAbsence().getBoolean(ITimeManagementService.HALFDAY_END) ? 0.5f : 1f;
		} 
		
		return leaveDays;
	}

	@Override
	public void setAbsence(Act absence) {
		this.absence = absence;
	}

	@Override
	public Act getAbsence() {
		return absence;
	}

	@Override
	public Date getDate() {
		return date;
	}

	protected void notifyParent() {
		parent.publish(this);
	}

	@Override
	public void publish(TimeSheetElement simpleTimeSheetElement) {
		if (subscribers != null) {
			for (Runnable run:subscribers) {
				run.run();
			}
		}
		if (parent != null) {
			parent.publish(this);
		}
	}

	private List<Runnable>subscribers;
	
	@Override
	public void subscribe(Runnable run) {
		if (subscribers == null) {
			subscribers = new ArrayList<Runnable>();
		}
		subscribers.add(run);
	}
	
}

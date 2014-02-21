package com.ceres.cldoc.timemanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleTimeSheetElement implements TimeSheetElement {

	private Date date;
	private int quota;
	private int workingTime;
	private AbsenceType absenceType = AbsenceType.NONE;
	
	private List<TimeSheetElement> children;

	public SimpleTimeSheetElement() {
	}

	public SimpleTimeSheetElement(Date date, int quota) {
		this.date = date;
		this.quota = quota;
	}

	@Override
	public int getQuota() {
		int q;
		if (isAbsent()) {
			q = 0;
		} else {
			q = quota;
			if (hasChildren()) {
				for (TimeSheetElement tse:getChildren()) {
					q += tse.getQuota();
				}
			}
		}
		return q;
	}

	public boolean isAbsent() {
		return !absenceType.equals(AbsenceType.NONE);
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
		return getWorkingTime() - getQuota();
	}

	public void setWorkingTime(int workingTime) {
		this.workingTime = workingTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("SimpleTimeSheetElement [absent=" + getAbsences() + ", quota=" + getQuota() + ", workingTime="
				+ getWorkingTime() + "(" + getBalance() + ")]\n");
		if (hasChildren()) {
			for (TimeSheetElement tse:getChildren()) {
				sb.append(tse.toString());  
			}
		}

		return sb.toString();
	}

	
	@Override
	public int getAbsences() {
		int absences = 0 ;
		
		if (isAbsent()) {
			absences = 1;
		} else {
			if (hasChildren()) {
				for (TimeSheetElement tse:getChildren()) {
					absences += tse.getAbsences();  
				}

			}
		}
		return absences;
	}

	@Override
	public void setAbsence(AbsenceType absenceType) {
		this.absenceType = absenceType;
	}

	@Override
	public AbsenceType getAbsenceType() {
		return absenceType;
	}

	@Override
	public Date getDate() {
		return date;
	}

	
	
}

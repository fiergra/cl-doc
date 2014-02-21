package com.ceres.cldoc.timemanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface TimeSheetElement extends Serializable {
	enum AbsenceType {NONE,SICK,HOLIDAY};
	
	Date getDate();
	int getQuota();
	int getWorkingTime();
	int getBalance();
	
	void setAbsence(AbsenceType absenceType);
	AbsenceType getAbsenceType();
	int getAbsences();
	boolean isAbsent();
	
	boolean hasChildren();
	List<TimeSheetElement> getChildren();
	void addChild(TimeSheetElement element);

}

package com.ceres.cldoc.timemanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;

public interface TimeSheetElement extends Serializable {
	enum AbsenceType {NONE,SICK,HOLIDAY};
	
	Date getDate();
	int getQuota();
	int getWorkingTime();
	int getBalance();
	
	void setAbsence(Act absence);
	Act getAbsence();
	float getAnnualLeaveDays();
	boolean isAbsent();
	
	boolean hasChildren();
	List<TimeSheetElement> getChildren();
	void addChild(TimeSheetElement element);
	
	void publish(TimeSheetElement simpleTimeSheetElement);
	void subscribe(Runnable run);
	
}

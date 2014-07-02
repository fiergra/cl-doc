package com.ceres.cldoc.client.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.cldoc.Session;
import com.google.gwt.user.client.rpc.AsyncCallback;



public interface TimeManagementServiceAsync {
	void getWorkPattern(Session session, AsyncCallback<WorkPattern> callback);
	void getWorkPattern(Session session, Person person, AsyncCallback<WorkPattern> callback);
	void setWorkPattern(Session session, Person person, Entity wp, Date startFromMonth, AsyncCallback<Void> callback);
	
	void loadTimeSheetYear(Session session, Person person, int year, AsyncCallback<TimeSheetYear> callback);
}

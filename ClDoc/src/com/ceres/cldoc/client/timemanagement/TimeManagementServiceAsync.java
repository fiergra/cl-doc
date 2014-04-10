package com.ceres.cldoc.client.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;



public interface TimeManagementServiceAsync {
	void getWorkPattern(ISession session, AsyncCallback<WorkPattern> callback);
	void getWorkPattern(ISession session, Person person, AsyncCallback<WorkPattern> callback);
	void setWorkPattern(ISession session, Person person, Entity wp, Date startFromMonth, AsyncCallback<Void> callback);
	
	void loadTimeSheetYear(ISession session, Person person, int year, AsyncCallback<TimeSheetYear> callback);
}

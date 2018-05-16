package eu.europa.ec.digit.shared;

import java.io.Serializable;

import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.shared.IAppointmentAction.ActionType;

public class WebSocketNotification implements Serializable {
	private static final long serialVersionUID = 983849139758221536L;
	public ActionType actionType;
	public Appointment appointment;
	
	protected WebSocketNotification() {}
	
	public WebSocketNotification(ActionType actionType, Appointment a) {
		this.actionType = actionType;
		this.appointment = a;
	}
	
}
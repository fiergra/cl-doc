package eu.europa.ec.digit.shared;

import eu.europa.ec.digit.eAgenda.Appointment;

public interface IAppointmentAction {
	public enum ActionType { insert, update, delete, subscribe };
	void action(ActionType t, Appointment a);
}

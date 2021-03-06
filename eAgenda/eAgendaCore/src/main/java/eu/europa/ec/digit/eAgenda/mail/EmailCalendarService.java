package eu.europa.ec.digit.eAgenda.mail;

import java.util.Date;
import java.util.List;

import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.IResource;

public interface EmailCalendarService {
	void removeAppointmentFromCalendar(final String id, String cancellationMessageText) throws Exception;

	boolean addAppointmentIntoCalendar(String[] recipients, String subject, String message, Appointment appointment) throws Exception;

	void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String bodyContent, String attachmentName, byte[] content) throws Exception;

	List<Appointment> getFreeBusyInfo(IResource host, Date startDate) throws Exception;

	void monitorInbox() throws Exception;

}

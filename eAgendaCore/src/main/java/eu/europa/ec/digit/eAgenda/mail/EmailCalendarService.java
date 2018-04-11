package eu.europa.ec.digit.eAgenda.mail;

import java.util.Date;
import java.util.List;

import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.IResource;

public interface EmailCalendarService {
	void removeAppointmentFromOutlookCalendar(final String id) throws Exception;

	boolean addAppointmentIntoOutlookCalendar(String subject, String message, Appointment appointment) throws Exception;

	void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String bodyContent, String attachmentName, byte[] content) throws Exception;

	List<Appointment> getFreeBusyInfo(IResource host, Date startDate) throws Exception;

}

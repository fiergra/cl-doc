package eu.europa.ec.digit.eAgenda.mail;

import java.util.Date;
import java.util.List;

import eu.europa.ec.digit.eAgenda.Appointment;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;

public interface ILightMSExchangeService {
	void removeAppointmentFromOutlookCalendar(final String id) throws Exception;

	boolean addAppointmentIntoOutlookCalendar(String[] recipients, String subject, String location, String message, Appointment appointment) throws Exception;

	void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String bodyContent, String attachmentName, byte[] content) throws Exception;

	List<AttendeeAvailability> getFreeBusyInfo(List<String> addresses, Date startDate, Date endDate) throws Exception;

}

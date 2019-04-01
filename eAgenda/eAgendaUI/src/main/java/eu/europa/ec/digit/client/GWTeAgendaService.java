package eu.europa.ec.digit.client;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europa.ec.digit.client.i18n.StringResource;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.shared.UserContext;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("eagenda")
public interface GWTeAgendaService extends RemoteService {
	List<IResource> findResources(String filter);
	List<User> findPersons(String filter);
	List<Room> findRooms(String filter);
	List<Campaign> listCampaigns();
	
	Campaign saveCampaign(Campaign c);
	Campaign deleteCampaign(Campaign c);
	Campaign findCampaign(String idOrName);
	
	Appointment cancelAppointment(Appointment a);
	Appointment saveAppointment(Campaign c, Appointment a);
	List<Appointment> getAppointments(Date d, IResource host, IResource guest);
	List<Appointment> getAppointments(Date d, Date until, IResource host, IResource guest);
	
	Collection<Date> loadHolidays(String cityCode);
	
	UserContext login(String userName);

	void saveStringResource(StringResource sr);
	HashMap<String, StringResource> getStringResources();
	UserContext login();
	
	boolean monitorInbox();
}

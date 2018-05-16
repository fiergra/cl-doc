package eu.europa.ec.digit.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europa.ec.digit.client.i18n.StringResource;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.shared.UserContext;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GWTeAgendaServiceAsync {
	void login(String userId, AsyncCallback<UserContext> callback);
	void saveStringResource(StringResource sr, AsyncCallback<Void> callback);
	void getStringResources(AsyncCallback<HashMap<String, StringResource>> callback);
	
	void findResources(String filter, AsyncCallback<List<IResource>> callback);
	void findPersons(String filter, AsyncCallback<List<User>> callback);
	void findRooms(String filter, AsyncCallback<List<Room>> callback);

	void findCampaign(String idOrName, AsyncCallback<Campaign> callback);
	void listCampaigns(AsyncCallback<List<Campaign>> callback);
	void saveCampaign(Campaign campaign, AsyncCallback<Campaign> callback);
	void deleteCampaign(Campaign campaign, AsyncCallback<Campaign> callback);
	
	void saveAppointment(Campaign c, Appointment a, AsyncCallback<Appointment> callback);
	void getAppointments(Date d, Date until, IResource host, IResource guest, AsyncCallback<List<Appointment>> callback);
	void getAppointments(Date d, IResource host, IResource guest, AsyncCallback<List<Appointment>> callback);
	void cancelAppointment(Appointment a, AsyncCallback<Appointment> rpcCallback);
}

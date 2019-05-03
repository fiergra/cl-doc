package eu.europa.ec.digit.client;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europa.ec.digit.athena.workflow.FiniteStateMachine;
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
	void login(AsyncCallback<UserContext> callback);
	void login(String userId, AsyncCallback<UserContext> callback);
	void saveStringResource(StringResource sr, AsyncCallback<Void> callback);
	void getStringResources(AsyncCallback<Map<String, StringResource>> callback);
	
	void findResources(String filter, AsyncCallback<List<IResource>> callback);
	void findPersons(String filter, AsyncCallback<List<User>> callback);
	void findRooms(String filter, AsyncCallback<List<Room>> callback);

	void findCampaign(String idOrName, AsyncCallback<Campaign> callback);
	void listCampaigns(AsyncCallback<List<Campaign>> callback);
	void saveCampaign(Campaign campaign, AsyncCallback<Campaign> callback);
	void deleteCampaign(Campaign campaign, AsyncCallback<Campaign> callback);
	
	void saveAppointment(Campaign c, Appointment a, AsyncCallback<Appointment> callback);
	void getAppointments(Date d, Date until, IResource host, IResource guest, boolean complete, AsyncCallback<List<Appointment>> callback);
	void getAppointments(Date d, IResource host, IResource guest, AsyncCallback<List<Appointment>> callback);
	void cancelAppointment(Appointment a, AsyncCallback<Appointment> rpcCallback);
	
	void loadHolidays(String cityCode, AsyncCallback<Collection<Date>> callback);

	void monitorInbox(AsyncCallback<Boolean> callback);
	void export(Campaign c, AsyncCallback<byte[]> callback);

	void applyAction(String workflowName, FiniteStateMachine wdef, Appointment a, String action, AsyncCallback<Appointment> rpcCallback);
	
	void send(Date date, AsyncCallback<Void> callback);
}

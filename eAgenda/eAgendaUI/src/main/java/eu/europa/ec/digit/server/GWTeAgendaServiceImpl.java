package eu.europa.ec.digit.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.ServerEndpointConfig;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.cec.digit.ecas.client.jaas.DetailedUser;
import eu.europa.ec.digit.client.ClientDateFormatter;
import eu.europa.ec.digit.client.GWTeAgendaService;
import eu.europa.ec.digit.client.i18n.StringResource;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Holiday;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.MongoAgendaService;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.mail.EmailCalendarService;
import eu.europa.ec.digit.eAgenda.mail.ExchangeEmailCalendarService;
import eu.europa.ec.digit.eAgenda.mail.ExchangeEmailCalendarService.IAppointmentListener;
import eu.europa.ec.digit.shared.IAppointmentAction.ActionType;
import eu.europa.ec.digit.shared.UserContext;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GWTeAgendaServiceImpl extends RemoteServiceServlet implements GWTeAgendaService {

	static {
		ServerEndpointConfig.Builder.create(UpdateWebSocketServer.class, "/appointments").build();
	}

	
	private static class DummyEmailService implements EmailCalendarService {
		
		private static Logger logger = Logger.getLogger("DummyEmailService");

		@Override
		public void removeAppointmentFromCalendar(String id) throws Exception {
			logger.info("removeAppointmentFromCalendar: " + id);
		}

		@Override
		public boolean addAppointmentIntoCalendar(String[] recipients, String subject, String message,	Appointment appointment) throws Exception {
			logger.info("addAppointmentIntoCalendar: to: " + recipients + " subject: " + subject + " message: " + message + " id: " + appointment.objectId);
			return true;
		}

		@Override
		public void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String message, String attachmentName, byte[] content) throws Exception {
			logger.info("sendMessage: to: " + recipients + " subject: " + subject + " message: " + message);
		}

		@Override
		public List<Appointment> getFreeBusyInfo(IResource host, Date startDate) throws Exception {
			logger.info("getFreeBusyInfo of " + host.getDisplayName() + " at " + ClientDateFormatter.format(startDate));
			return null;
		}
		
	}
	
	private EmailCalendarService ecs;
	
	private boolean isProduction() {
		String serverName = getThreadLocalRequest().getServerName();
		
		return serverName.contains("tccp0060");
		
	}
	
	private synchronized EmailCalendarService getEmailCalendarService() {
		if (ecs == null) {
			if (isProduction()) {
				try {
					InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("email.properties");
					Properties properties = new Properties();
					properties.load(in);
					String userName = properties.getProperty("userName"); 
					String passWord = properties.getProperty("passWord"); 
					String emailAddress = properties.getProperty("emailAddress"); 
					IAppointmentListener listener = new IAppointmentListener() {
	
						private void setState(String objectId, String state) {
							Appointment a = getMc().findAppointment(objectId);
							if (a != null) {
								a.state = state;
								getMc().saveAppointment(a);
								UpdateWebSocketServer.notifyAll(ActionType.update, a);
							}
						}
	
						@Override
						public void accepted(String objectId) {
							setState(objectId, "ACCEPTED");
						}
	
						@Override
						public void decline(String objectId) {
							Appointment a = getMc().findAppointment(objectId);
							if (a != null) {
								cancelAppointment(a);
							}
						}
	
						@Override
						public void tentative(String objectId) {
							setState(objectId, "TENTATIVE");
						}
					};
					ecs = new ExchangeEmailCalendarService(userName, passWord, emailAddress, listener);
				} catch (Exception e) {
					ecs = new DummyEmailService();			
					e.printStackTrace();
				}
			} else {
				ecs = new DummyEmailService();
			}
		}
		return ecs;
	}
	
	private MongoAgendaService mc;
	
	private MongoAgendaService getMc() {
		if (mc == null) {
			try {
				mc = new MongoAgendaService();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return mc;
	}
	
	@Override
	public List<Campaign> listCampaigns() {
		if (getUserContext() != null) {
			return getMc().getCampaigns(getUserContext().user);
		}
		return null;
	}

	@Override
	public Campaign deleteCampaign(Campaign c) {
		getMc().deleteCampaign(c);
		return c;
	}

	@Override
	public Campaign saveCampaign(Campaign c) {
		getMc().saveCampaign(c);
		return c;
	}

	@Override
	public List<User> findPersons(String filter) {
		return getMc().findPersons(filter);
	}

	@Override
	public List<Room> findRooms(String filter) {
		return getMc().findRooms(filter);
	}

	@Override
	public Appointment saveAppointment(Campaign c, Appointment a) {
		ActionType actionType = a.objectId != null ? ActionType.update : ActionType.insert;
		getMc().saveAppointment(a);
		UpdateWebSocketServer.notifyAll(actionType, a);
		
		if (c != null) {
			try {
				
				String[] recipients = new String[] { a.guest.getEMailAddress() != null ? a.guest.getEMailAddress() : getConnectedUserEmail() };
				
				String messageBody;
				
				if (!getUserContext().user.equals(a.guest)) {
					messageBody = "<b>" + " this appointment has been created by " + getUserContext().user.getDisplayName() + " on behalf of " + a.guest.getDisplayName() + "</b><br/><br/>";
				} else {
					messageBody = "";
				}
				
				messageBody += c.emailSettings.body;
				getEmailCalendarService().addAppointmentIntoCalendar(recipients, c.emailSettings.subject, messageBody, a);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	@Override
	public Appointment cancelAppointment(Appointment a) {
		getMc().deleteAppointment(a);
		UpdateWebSocketServer.notifyAll(ActionType.delete, a);
		try {
			getEmailCalendarService().removeAppointmentFromCalendar(a.objectId);//.toHexString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	@Override
	public List<IResource> findResources(String filter) {
		return getMc().findResources(filter);
	}

	private static final DateFormat df = DateFormat.getDateInstance();

	private Date checkUntil(Date from, Date until) {
		if (until == null) {
			try {
				until = new Date(df.parse(df.format(from)).getTime()  + 24L * 60L * 60L * 1000L);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return until;
	}
	
	@Override
	public List<Appointment> getAppointments(Date from, Date until, IResource host, IResource guest) {
		List<Appointment> appointments = getMc().getAppointments(from, checkUntil(from, until), host, guest);
		
		return addFreeBusyInfo(appointments, from, until, host);
	}

	private List<Appointment> addFreeBusyInfo(List<Appointment> appointments, Date from, Date until, IResource host) {
		if (host != null) {
		try {
			List<Appointment> freeBusy = getEmailCalendarService().getFreeBusyInfo(host, from);
			if (freeBusy != null) {
				freeBusy = freeBusy.parallelStream().filter(a -> a.from.getTime() < until.getTime() && a.until.getTime() >= from.getTime()).collect(Collectors.toList());
				appointments.addAll(freeBusy);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		return appointments;
	}

	@Override
	public List<Appointment> getAppointments(Date from, IResource host, IResource guest) {
		Date until = checkUntil(from, null);
		List<Appointment> appointments = getMc().getAppointments(from, checkUntil(from, until), host, guest);
		return addFreeBusyInfo(appointments, from, until, host);
	}

	@Override
	public Campaign findCampaign(String idOrName) {
		return getMc().findCampaign(idOrName);
	}

	private DetailedUser getDetailedUser() {
		HttpServletRequest request = getThreadLocalRequest(); 		
		Principal principal = request != null ? request.getUserPrincipal() : null;
		
		return principal instanceof DetailedUser ? (DetailedUser)principal : null;
	}
	
	@Override
	public UserContext login() {
		DetailedUser du = getDetailedUser();
		return du != null ? login(du.getName()) : null;
	}
	
	private String getConnectedUserEmail() {
		DetailedUser du = getDetailedUser();
		return du != null ? du.getEmail() : "ralph.fiergolla@ec.europa.eu";
	}
	
	@Override
	public UserContext login(String userName) {
		User user = getMc().getUser(userName);
		UserContext userContext = null;
		
		if (user != null ) {
			userContext = new UserContext(user, getRoles(user));
			HttpSession httpSession = getThreadLocalRequest().getSession();
			httpSession.setAttribute(UserContext.USERCONTEXT, userContext);
		}
	
		return userContext;
	}
	
	private UserContext getUserContext() {
		HttpSession httpSession = getThreadLocalRequest().getSession();
		return (UserContext) httpSession.getAttribute(UserContext.USERCONTEXT);
	}

	private Collection<String> getRoles(User user) {
		Collection<String> roles = new ArrayList<String>();
		
		if (user.userId.equals("fiergra")) {
			roles.add(UserContext.ADMIN);
			roles.add("campaignmanager");
		}
		
		return roles;
	}
	
	@Override
	public void saveStringResource(StringResource sr) {
		getMc().saveStringResource(sr);
	}
	
	@Override
	public HashMap<String, StringResource> getStringResources() {
		return getMc().getStringResources();
	}

	@Override
	public Collection<Date> loadHolidays(String cityCode) {
		List<Holiday> holidays = getMc().loadHolidays(cityCode);
		HashSet<Date> sHolidays = new HashSet<>();
		holidays.forEach(h -> sHolidays.add(h.date));
		return sHolidays;
	}
}

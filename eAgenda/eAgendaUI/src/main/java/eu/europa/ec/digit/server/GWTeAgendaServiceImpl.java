package eu.europa.ec.digit.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.ServerEndpointConfig;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ibm.icu.util.Calendar;

import eu.cec.digit.ecas.client.jaas.DetailedUser;
import eu.europa.ec.digit.athena.workflow.FiniteStateMachine;
import eu.europa.ec.digit.athena.workflow.FiniteStateMachine.Transition;
import eu.europa.ec.digit.athena.workflow.WorkflowDefinition;
import eu.europa.ec.digit.athena.workflow.WorkflowInstance;
import eu.europa.ec.digit.athena.workflow.WorkflowState;
import eu.europa.ec.digit.athena.workflow.WorkflowTransition;
import eu.europa.ec.digit.athena.workflow.service.AbstractTransitionListener;
import eu.europa.ec.digit.athena.workflow.service.AbstractWorkflowService;
import eu.europa.ec.digit.athena.workflow.service.TransitionListener;
import eu.europa.ec.digit.client.GWTeAgendaService;
import eu.europa.ec.digit.client.i18n.StringResource;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Holiday;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.MongoAgendaService;
import eu.europa.ec.digit.eAgenda.Person;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkflowService;
import eu.europa.ec.digit.eAgenda.mail.EmailCalendarService;
import eu.europa.ec.digit.eAgenda.mail.ExchangeEmailCalendarService;
import eu.europa.ec.digit.eAgenda.mail.ExchangeEmailCalendarService.IAppointmentListener;
import eu.europa.ec.digit.shared.IAppointmentAction.ActionType;
import eu.europa.ec.digit.shared.UserContext;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GWTeAgendaServiceImpl extends RemoteServiceServlet implements GWTeAgendaService {

	private static Logger logger = Logger.getLogger("GWTeAgendaService");

	static {
		ServerEndpointConfig.Builder.create(UpdateWebSocketServer.class, "/appointments").build();
	}

	private static class DummyEmailService implements EmailCalendarService {

		private static Logger logger = Logger.getLogger("DummyEmailService");

		@Override
		public void removeAppointmentFromCalendar(String id, String cancellationMessageText) throws Exception {
			logger.info("removeAppointmentFromCalendar: " + id + ". " + cancellationMessageText);
		}

		@Override
		public boolean addAppointmentIntoCalendar(String[] recipients, String subject, String message, Appointment appointment) throws Exception {
			logger.info("addAppointmentIntoCalendar: to: " + recipients + " subject: " + subject + " message: " + message + " id: " + appointment.objectId);
			return true;
		}

		@Override
		public void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String message, String attachmentName, byte[] content) throws Exception {
			logger.info("sendMessage: to: " + recipients + " subject: " + subject + " message: " + message);
		}

		@Override
		public List<Appointment> getFreeBusyInfo(IResource host, Date startDate) throws Exception {
			logger.info("getFreeBusyInfo of " + host.getDisplayName() + " at " + startDate);
			return null;
		}

		@Override
		public void monitorInbox() throws Exception {
			logger.info("monitorInbox...");
		}

	}

	private EmailCalendarService ecs;

	private boolean isProduction() {
		// return true;

		String serverName = getThreadLocalRequest().getServerName();
		boolean isProd = serverName.contains("tccp0060");

		if (isProd) {
			logger.info("*******************************************");
			logger.info("***** running on production server ********");
			logger.info("*******************************************");
		} else {
			logger.info("running on server: " + serverName);
		}
		return isProd;

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
			String cancellationText;
			if (!getUserContext().user.equals(a.guest)) {
				cancellationText = "This appointment has been cancelled by " + getUserContext().user.getDisplayName() + " on behalf of " + a.guest.getDisplayName();
			} else {
				cancellationText = null;
			}
			getEmailCalendarService().removeAppointmentFromCalendar(a.objectId, cancellationText);// .toHexString());
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
				until = new Date(df.parse(df.format(from)).getTime() + 24L * 60L * 60L * 1000L);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return until;
	}

	@Override
	public List<Appointment> getAppointments(Date from, Date until, IResource host, IResource guest, boolean complete) {
		List<Appointment> appointments = getMc().getAppointments(from, checkUntil(from, until), host, guest);
		
		if (!complete) {
			appointments.forEach(a -> a.guest = null);
		}

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

		return principal instanceof DetailedUser ? (DetailedUser) principal : null;
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

		if (user != null) {
			userContext = new UserContext(user, getRoles(user));
			userContext.builtAt = getBuildTimestamp();
			HttpSession httpSession = getThreadLocalRequest().getSession();
			httpSession.setAttribute(UserContext.USERCONTEXT, userContext);
		}

		return userContext;
	}

	private String getBuildTimestamp() {
		String timestamp = "<unknown>";
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("build.properties");
			Properties properties = new Properties();
			properties.load(in);
			timestamp = properties.getProperty("build.timestamp", "<unknown>");
		} catch (IOException x) {
			timestamp = x.getMessage();
		}
		return timestamp;
	}

	private UserContext getUserContext() {
		HttpSession httpSession = getThreadLocalRequest().getSession();
		return (UserContext) httpSession.getAttribute(UserContext.USERCONTEXT);
	}

	private Collection<String> getRoles(User user) {
		Collection<String> roles = new ArrayList<String>();

		if (user.userId.equals("fiergra") || user.userId.equals("lilpepe")) {
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

	@Override
	public boolean monitorInbox() {
		try {
			getEmailCalendarService().monitorInbox();
			return true;
		} catch (Exception e) {
			logger.warning(e.getMessage());
			return false;
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String export = req.getParameter("export");
		
		if (export == null) {
			super.doGet(req, resp);
		} else {
			Campaign campaign = getMc().findCampaign(export);
			if (campaign != null) {
				byte[] data = export(campaign);
				serveBytes(resp, campaign.name + ".xls", "application/vnd.ms-excel", data);
			} else {
				resp.sendError(404);
			}
					
		}
	}
	
	private void serveBytes(HttpServletResponse resp, String name, String mimeType, byte[] data) throws IOException {
		logger.info("serving " + data.length + " bytes.");
		resp.setHeader("Content-Disposition", "inline; filename=" + name);
		resp.setContentType(mimeType);
		resp.setContentLength(data.length);
		ServletOutputStream out = resp.getOutputStream();
		out.write(data);
		out.flush();
	}


	@Override
	public byte[] export(Campaign campaign) {
		List<IResource> hosts = campaign.assignedResources();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date start = calendar.getTime();
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date end = calendar.getTime();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			WritableWorkbook workbook = Workbook.createWorkbook(outputStream);

			List<Appointment> appointments = getMc().getAppointments(start, end, null, null);
			addSheet(workbook, null, appointments);
			for (IResource host : hosts) {
				appointments = getMc().getAppointments(start, end, host, null);
				addSheet(workbook, host, appointments);
			}
			
			workbook.write();
			workbook.close();

		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
		return outputStream.toByteArray();
	}

	private void addSheet(WritableWorkbook workbook, IResource host, List<Appointment> appointments) throws Exception {
		WritableSheet sheet = workbook.createSheet(host != null ? host.getDisplayName() : "All", 0);
		int col = 0;
		
		if (host == null) {
			addHeaderCell(sheet, col++, "Location");
		}
		addHeaderCell(sheet, col++, "Date");
		addHeaderCell(sheet, col++, "Time");
		addHeaderCell(sheet, col++, "Last name");
		addHeaderCell(sheet, col++, "First name");
		addHeaderCell(sheet, col++, "Email");
		addHeaderCell(sheet, col++, "Login");

		int row = 0;

		WritableCellFormat dateFormat = new jxl.write.WritableCellFormat(new jxl.write.DateFormat("d/m/yyyy"));
		WritableCellFormat timeFormat = new jxl.write.WritableCellFormat(new jxl.write.DateFormat("h:mm"));
		appointments.sort((a1, a2) -> a1.from.compareTo(a2.from));
		for (Appointment a : appointments) {
			col = 0;
			row++;

			Person guest = a.guest.person;
			if (host == null) {
				sheet.addCell(new Label(col, row, a.host.getDisplayName()));
				sheet.setColumnView(col, 20);
				col++;
			}

			sheet.addCell(new DateTime(col, row, a.from, dateFormat));
			sheet.setColumnView(col, 20);
			col++;
			sheet.addCell(new DateTime(col, row, a.from, timeFormat));
			sheet.setColumnView(col, 20);
			col++;
			sheet.addCell(new Label(col, row, guest.lastName));
			sheet.setColumnView(col, 50);
			col++;
			sheet.addCell(new Label(col, row, guest.firstName));
			sheet.setColumnView(col, 50);
			col++;
			sheet.addCell(new Label(col, row, a.guest.getEMailAddress()));
			sheet.setColumnView(col, 50);
			col++;
			sheet.addCell(new Label(col, row, a.guest.userId));
			sheet.setColumnView(col, 50);
			col++;
		}

	}

	public void addHeaderCell(WritableSheet sheet, int column, String label) throws Exception {
		WritableFont cellFont = new WritableFont(WritableFont.ARIAL);
		cellFont.setBoldStyle(WritableFont.BOLD);
		cellFont.setColour(Colour.WHITE);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setBackground(Colour.GRAY_80);
		sheet.addCell(new Label(column, 0, label, cellFormat));
	}

	public static void main(String[] args) throws IOException {
		GWTeAgendaServiceImpl service = new GWTeAgendaServiceImpl();
		
		List<Campaign> campaigns = service.getMc().getCampaigns(service.getMc().getUser("fiergra"));
		for (Campaign c:campaigns) {
			byte[] data = service.export(c);
			File file = File.createTempFile(c.objectId, ".xls");
			System.out.println(file.getAbsolutePath());
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(data);
			fout.close();
		}
	}
	
	private AbstractWorkflowService<Object> workflowService;
	
	public AbstractWorkflowService<Object> getWorkflowService() {
		if (workflowService == null) {
			workflowService = new WorkflowService();
			TransitionListener<Object> listener = new AbstractTransitionListener<Object>() {

				@Override
				public void onTransition(WorkflowInstance instance, WorkflowTransition t, Object payload) {
					getMc().log(instance, t, payload);
				}
			};
			workflowService.addTransititionListener(listener);		
		}
		return workflowService;
	}
	
	
	@Override
	public Appointment applyAction(String workflowName, FiniteStateMachine fsm, Appointment appointment, String action) {
		if (getWorkflowService().apply(new WorkflowInstance(asWorkflowDefinition(fsm), appointment.state != null ? appointment.state : "invited") {

			@Override
			public void setCurrentState(WorkflowState currentState) {
				if (appointment.states != null) {
					appointment.states.put(workflowName, currentState.name);
				} else {
					appointment.state = currentState.name;
				}
			}
			
		}, action, null)) {
			getMc().saveAppointment(appointment);
			UpdateWebSocketServer.notifyAll(ActionType.update, appointment);

		};
		return appointment;
	}

	private WorkflowDefinition asWorkflowDefinition(FiniteStateMachine workflowDefinition) {
		WorkflowDefinition wDef = new WorkflowDefinition("fsm");
		for (Transition t:workflowDefinition.getTransitions()) {
			wDef.addTransition(new WorkflowTransition(null, t.currentState, t.nextState, t.input));
		}
		return wDef;
	}
}

package eu.europa.ec.digit.eAgenda.mail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import eu.europa.ec.digit.eAgenda.AppointmentType;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.IResource;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.availability.FreeBusyViewType;
import microsoft.exchange.webservices.data.core.enumeration.availability.SuggestionQuality;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.misc.TraceFlags;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.LegacyFreeBusyStatus;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.MeetingResponseType;
import microsoft.exchange.webservices.data.core.enumeration.property.Sensitivity;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendCancellationsMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsOrCancellationsMode;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.MeetingResponse;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ITraceListener;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.AvailabilityOptions;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.notification.ItemEvent;
import microsoft.exchange.webservices.data.notification.NotificationEvent;
import microsoft.exchange.webservices.data.notification.NotificationEventArgs;
import microsoft.exchange.webservices.data.notification.StreamingSubscription;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection.INotificationEventDelegate;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection.ISubscriptionErrorDelegate;
import microsoft.exchange.webservices.data.notification.SubscriptionErrorEventArgs;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.AttendeeCollection;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.availability.CalendarEvent;
import microsoft.exchange.webservices.data.property.complex.availability.CalendarEventDetails;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

public class ExchangeEmailCalendarService implements EmailCalendarService {
	
	public interface IAppointmentListener {

		void accepted(String objectId);

		void decline(String objectId);

		void tentative(String objectId);}
	
	private final IAppointmentListener appointmentListener;

	public static Logger log = Logger.getLogger(ExchangeEmailCalendarService.class.getName());

	public static enum MSExchangeFolderEnum {
		INBOX(WellKnownFolderName.Inbox.name()), CALENDAR(WellKnownFolderName.Calendar.name()), DEV("DEV");

		private String folderName;

		MSExchangeFolderEnum(String folderName) {
			this.folderName = folderName;
		}

		public String getFolderName() {
			return folderName;
		}
	};

	protected UUID _UUID = UUID.fromString("895b30a3-5e2b-4895-9043-5bc297dbae83");
	protected String _CUSTOM_PROPERTY_GEN_ITEM = "GEN_ITEM_ID";

	private boolean initialized = false;

	private String emailAddress;

	private Mailbox mailBox;
	private ExchangeService service;

	private synchronized void setExchangeService(String keyUser, String keyPass, String emailAddress) throws Exception {
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);// Exchange2007_SP1);
		service.setExchange2007CompatibilityMode(true);
		ExchangeCredentials credentials = new WebCredentials(keyUser, keyPass);
		service.setCredentials(credentials);
		service.setTraceEnabled(false);
		service.setTraceFlags(EnumSet.allOf(TraceFlags.class)); 
		service.setTraceListener(new ITraceListener() {
			
			@Override
			public void trace(String traceType, String traceMessage) {
				if(initialized == false) {
					log.info("Type:" + traceType + " Message:" + traceMessage);
				}
			}
		});
		service.autodiscoverUrl(emailAddress);
		mailBox = new Mailbox(emailAddress);
		this.emailAddress = emailAddress;
		initialized = true;
		
		
		
//		service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
//		service.setExchange2007CompatibilityMode(true);
//		ExchangeCredentials credentials = new WebCredentials(keyUser, keyPass);
//		service.setCredentials(credentials);
//		//service.setEnableScpLookup(true);
//		service.setTraceEnabled(true);
//		service.autodiscoverUrl(emailAddress);
//		//service.setUrl(new java.net.URI("https://webmail.ec.europa.eu/EWS/Exchange.asmx"));
//		mailBox = new Mailbox(emailAddress);
//		this.emailAddress = emailAddress;
//		initialized = true;
//		
		monitorInbox();
	}

	public ExchangeEmailCalendarService(String exchangeUser, String exchangePass, String fmb, IAppointmentListener appointmentListener) throws Exception {
		this.appointmentListener = appointmentListener;
		init(exchangeUser, exchangePass, fmb);
	}

	private void init(String keyUser, String keyPass, String emailAddress) throws Exception {
		if (!isInitialized()) {
			setExchangeService(keyUser, keyPass, emailAddress);
		}
	}

	@Override
	public void removeAppointmentFromCalendar(final String id, String cancellationMessageText) throws Exception {

		synchronized (service) {
			Appointment ooApt = getExistingOutlookAppointment(id);
			if (ooApt != null) {
				if (cancellationMessageText != null) {
					ooApt.cancelMeeting(cancellationMessageText);
				}
				ooApt.delete(DeleteMode.MoveToDeletedItems, SendCancellationsMode.SendOnlyToAll);
			}
		}
	}

	public Appointment getExistingOutlookAppointment(String id) {
		Appointment toReturn = null;

		synchronized (service) {

			try {
				ExtendedPropertyDefinition extPropDef = new ExtendedPropertyDefinition(_UUID, _CUSTOM_PROPERTY_GEN_ITEM, MapiPropertyType.String);
				PropertySet extendedPropertySet = new PropertySet(BasePropertySet.FirstClassProperties, extPropDef);
				SearchFilter filter = new SearchFilter.IsEqualTo(AppointmentSchema.InReplyTo, id);
				ItemView view = new ItemView(100);
				view.setPropertySet(extendedPropertySet);
				FolderId fdi = new FolderId(WellKnownFolderName.Calendar, mailBox);
				FindItemsResults<Item> appointmentItems = service.findItems(fdi, filter, view);

				if (appointmentItems != null && appointmentItems.getTotalCount() != 0) {
					// no need because we will get always one element and the good one according to
					// the filter (which compares with the item id
					// we will keep it for safe but can be removed one day :-)
					for (Item item : appointmentItems.getItems()) {
						if (item instanceof Appointment) {
							Appointment appointment = (Appointment) Item.bind(service, item.getId(), extendedPropertySet);
							ExtendedProperty prop = appointment.getExtendedProperties().getPropertyAtIndex(0);
							if (prop.getValue().toString().equals(id)) {
								return (Appointment) appointment;
							}
						}
					}
				}
			} catch (Exception ex) {
				// throw new TechnicalException(201301091145l, ex,
				// "getExistingOutlookAppointment : Cannot find the EWS appointment :" +
				// ex.getMessage());
				log.warning("getExistingOutlookAppointment : Cannot find the EWS appointment :" + ex.getMessage());
				toReturn = null;
			}

			return toReturn;
		}
	}

	@Override
	public boolean addAppointmentIntoCalendar(String[] recipients, String subject, String message, eu.europa.ec.digit.eAgenda.Appointment agendaAppointment) throws Exception {
		boolean createdNew = false;

		synchronized (service) {
			String appointmentId = agendaAppointment.objectId;
			Date dateFrom = agendaAppointment.from;
			Date dateTo = agendaAppointment.until;
			String place = agendaAppointment.getLocation() != null ? agendaAppointment.getLocation().getDisplayName() : null;
			FolderId fid1 = new FolderId(WellKnownFolderName.Calendar, mailBox);

			Appointment existingAppointment = getExistingOutlookAppointment(appointmentId);
			// byte[] fileContent = outlookAppointment.getAttachment();
			// String fileName = outlookAppointment.getAttachmentFileName();

			if (existingAppointment == null) {
				Appointment calAppointment = new Appointment(service);
				ExtendedPropertyDefinition extPropDef = new ExtendedPropertyDefinition(_UUID, _CUSTOM_PROPERTY_GEN_ITEM, MapiPropertyType.String);
				calAppointment.setExtendedProperty(extPropDef, appointmentId);
				calAppointment.setInReplyTo(appointmentId.toString());
				calAppointment.setAllowNewTimeProposal(false);
				calAppointment.setIsResponseRequested(true);
				calAppointment.setSubject(subject);
				calAppointment.setBody(MessageBody.getMessageBodyFromText(message));
				calAppointment.setImportance(Importance.Normal);
				calAppointment.setStart(dateFrom);
				calAppointment.setEnd(dateTo);
				calAppointment.setIsReminderSet(true);
				calAppointment.setLocation(place);
				calAppointment.setReminderMinutesBeforeStart(30);
				calAppointment.setSensitivity(Sensitivity.Private);
				// if (fileContent != null) {
				// calAppointment.getAttachments().addFileAttachment(fileName, fileContent);
				// }

				for (String email : recipients) {
					calAppointment.getRequiredAttendees().add(email);
				}
				calAppointment.save(fid1, SendInvitationsMode.SendOnlyToAll);
//				calAppointment.update(ConflictResolutionMode.AutoResolve, SendInvitationsOrCancellationsMode.SendToChangedAndSaveCopy);
				createdNew = true;
			} else {

				if (anyChange(existingAppointment, subject, dateFrom, dateTo, place, message, recipients)) {
					existingAppointment.setSubject(subject);
					existingAppointment.setStart(dateFrom);
					existingAppointment.setEnd(dateTo);
					existingAppointment.setLocation(place);
					existingAppointment.setBody(MessageBody.getMessageBodyFromText(message));
					for (String email : recipients) {
						existingAppointment.getRequiredAttendees().add(email);
					}
					existingAppointment.update(ConflictResolutionMode.AlwaysOverwrite, SendInvitationsOrCancellationsMode.SendToAllAndSaveCopy);
				}
			}
		}
		return createdNew;
	}

	private boolean anyChange(Appointment existingAppointment, String subject, Date dateFrom, Date dateTo, String place, String message, String[] recipients) throws Exception {
		boolean anyChange = false;

		anyChange = anyChange || !existingAppointment.getSubject().equals(subject);
		anyChange = anyChange || !existingAppointment.getStart().equals(dateFrom);
		anyChange = anyChange || !existingAppointment.getEnd().equals(dateTo);
		anyChange = anyChange || !existingAppointment.getLocation().equals(place);

		if (!anyChange) {

			MessageBody newBody = MessageBody.getMessageBodyFromText(message);
			String newMessage = MessageBody.getStringFromMessageBody(newBody);
			newMessage = newMessage.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ").replaceAll("\\r|\\n", "").replaceAll(" ", "").trim();

			String oldMessage = MessageBody.getStringFromMessageBody(existingAppointment.getBody());
			oldMessage = oldMessage.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ").replaceAll("\\r|\\n", "").replaceAll(" ", "").trim();

			anyChange = anyChange || !oldMessage.equals(newMessage);

			AttendeeCollection attendees = existingAppointment.getRequiredAttendees();
			if (attendees.getCount() != recipients.length) {
				anyChange = true;
			} else {
				Iterator<Attendee> ai = attendees.iterator();
				List<String> rList = Arrays.asList(recipients);
				while (ai.hasNext() && !anyChange) {
					String address = ai.next().getAddress();
					anyChange = !rList.contains(address);
				}
			}

		}

		return anyChange;
	}

	@Override
	public void sendMessage(String requesterEmail, String[] recipients, String[] cc, String[] bcc, String subject, String bodyContent, String attachmentName, byte[] content) throws Exception {
		synchronized (service) {
			FolderId fid1 = new FolderId(WellKnownFolderName.SentItems, mailBox);
			EmailMessage mail = new EmailMessage(service);

			mail.setFrom(new EmailAddress(emailAddress));
			for (String smtpAddress : recipients) {
				mail.getToRecipients().add(new EmailAddress(smtpAddress));
			}

			if (cc != null && cc.length != 0) {
				for (String crecip : cc) {
					mail.getCcRecipients().add(new EmailAddress(crecip));
				}
			}

			if (bcc != null && bcc.length != 0) {
				for (String crecip : bcc) {
					mail.getBccRecipients().add(new EmailAddress(crecip));
				}
			}

			mail.getBccRecipients().add(mail.getFrom());
			mail.setSubject(subject);
			mail.setImportance(Importance.High);
			mail.setBody(MessageBody.getMessageBodyFromText(bodyContent));
			if (content != null) {
				FileAttachment fileAtt = mail.getAttachments().addFileAttachment(attachmentName, new ByteArrayInputStream(content));
				fileAtt.setContentType("application/pdf");
			}
			mail.save(fid1);
			mail.send();
		}
	}

	private boolean isInitialized() {
		return initialized;
	}


	@Override
	public List<eu.europa.ec.digit.eAgenda.Appointment> getFreeBusyInfo(IResource host, Date startDate) throws Exception {
		List<eu.europa.ec.digit.eAgenda.Appointment> result = new ArrayList<>();
		if (host.getEMailAddress() != null) {
			synchronized (service) {
				Date endDate = new Date(startDate.getTime() + 40 * 24 * 60 * 60 * 1000L);
				// Create a collection of attendees.
				List<AttendeeInfo> attendees = new ArrayList<AttendeeInfo>();
				attendees.add(AttendeeInfo.getAttendeeInfoFromString(host.getEMailAddress()));

				// Specify options to request free/busy information and suggested meeting times.
				AvailabilityOptions availabilityOptions = new AvailabilityOptions();
				availabilityOptions.setGoodSuggestionThreshold(49);
				availabilityOptions.setMaximumNonWorkHoursSuggestionsPerDay(0);
				availabilityOptions.setMaximumSuggestionsPerDay(2);
				// Note that 60 minutes is the default value for MeetingDuration, but setting it
				// explicitly for demonstration purposes.
				availabilityOptions.setMeetingDuration(30);
				availabilityOptions.setMergedFreeBusyInterval(1440);
				availabilityOptions.setMinimumSuggestionQuality(SuggestionQuality.Good);
				availabilityOptions.setDetailedSuggestionsWindow(new TimeWindow(startDate, endDate));
				availabilityOptions.setRequestedFreeBusyView(FreeBusyViewType.Detailed);
		
				// Return free/busy information and a set of suggested meeting times.
				// This method results in a GetUserAvailabilityRequest call to EWS.
				GetUserAvailabilityResults results = service.getUserAvailability(attendees, availabilityOptions.getDetailedSuggestionsWindow(), AvailabilityData.FreeBusy, availabilityOptions);
				for (AttendeeAvailability availability : results.getAttendeesAvailability()) {
					for (CalendarEvent ce : availability.getCalendarEvents()) {
						eu.europa.ec.digit.eAgenda.Appointment a = convert(host, ce);
						if (a != null) {
							result.add(a);
						}
					}
				}
			}
		}
		return result;
	}

	private eu.europa.ec.digit.eAgenda.Appointment convert(IResource host, CalendarEvent ce) {
		eu.europa.ec.digit.eAgenda.Appointment a = null;
		if (LegacyFreeBusyStatus.Busy.equals(ce.getFreeBusyStatus()) || LegacyFreeBusyStatus.OOF.equals(ce.getFreeBusyStatus())) {
			String itemClass = null;
			switch (ce.getFreeBusyStatus()) {
			case Busy:
				itemClass = "OUTLOOK_CALENDAR_EVENT";
				break;
			case OOF:
				itemClass = "OBT_ABSENCE_ITEM";
				break;
			default:
				itemClass = "MEETING";
			}
			// set dummy ID to avoid adding the same calendar event multiple times
//			String sId = ce.toString() + ce.getStartTime() + ce.getEndTime();
			a = new eu.europa.ec.digit.eAgenda.Appointment(new Campaign(), host, null, null, ce.getStartTime(), ce.getEndTime(), new AppointmentType(itemClass, 0, "yellow"));
			a.objectId = ce.toString();//new ObjectId();
			String comment = "";
			CalendarEventDetails details = ce.getDetails();
			if (details != null) {
				String location = details.getLocation();
				String subject = details.getLocation();
				if (subject != null) {
					comment += " \"" + subject + "\"";
				}
				if (location != null) {
					comment += " @" + location;
				}
			}
			a.comment = comment;
		}
		return a;
	}
	
	
	@Override
	public void monitorInbox() throws Exception {
		WellKnownFolderName sd = WellKnownFolderName.Inbox;
		FolderId folderid = new FolderId(sd);
		List<FolderId> folder = new ArrayList<FolderId>();
		folder.add(folderid);
		StreamingSubscription subscription = service.subscribeToStreamingNotifications(folder, EventType.NewMail, EventType.Created, EventType.Deleted, EventType.Modified);
		@SuppressWarnings("resource")
		StreamingSubscriptionConnection conn = new StreamingSubscriptionConnection(service, 30);
		conn.addSubscription(subscription);
		log.info("streaming subscription added.");
		INotificationEventDelegate listener = new INotificationEventDelegate() {

			@Override
			public void notificationEventDelegate(Object arg0, NotificationEventArgs eArgs) {
				Iterator<NotificationEvent> events = eArgs.getEvents().iterator();
				
				while (events.hasNext()) {
					NotificationEvent e = events.next();
					if (e instanceof ItemEvent) {
						ItemEvent ie = (ItemEvent)e;
						log.info("received item event: " + ie.getEventType().name());
						if (e.getEventType().equals(EventType.Created)) {
							try {
								synchronized (service) {
									Item item = Item.bind(service, ie.getItemId());
									if (item instanceof MeetingResponse) {
										MeetingResponse mr = (MeetingResponse)item;
										MeetingResponseType rt = mr.getResponseType();

										ItemId appointmentId = mr.getAssociatedAppointmentId();
										ExtendedPropertyDefinition extPropDef = new ExtendedPropertyDefinition(_UUID, _CUSTOM_PROPERTY_GEN_ITEM, MapiPropertyType.String);
										PropertySet extendedPropertySet = new PropertySet(BasePropertySet.FirstClassProperties, extPropDef);

										Appointment appointment = Appointment.bind(service, appointmentId, extendedPropertySet);
										ExtendedProperty prop = appointment.getExtendedProperties().getPropertyAtIndex(0);
										String objectId = prop.getValue().toString();

										log.info("meeting response for ID " + objectId + " ==> " + rt.name());

										if (appointmentListener != null) {
											switch (rt) {
											case Accept:
												appointmentListener.accepted(objectId);
												break;
											case Decline:
												appointmentListener.decline(objectId);
												break;
											case Tentative:
												appointmentListener.tentative(objectId);
												break;
											case NoResponseReceived:
											case Organizer:
											case Unknown:
											default:
												break;
											} 
										}
									}
								}
							} catch (Exception e1) {
								log.severe(e1.getLocalizedMessage());
							}
						}
					}
				}
			}
			
		};
		conn.addOnNotificationEvent(listener);
		conn.addOnDisconnect(new ISubscriptionErrorDelegate() {

			@Override
			public void subscriptionErrorDelegate(Object arg0, SubscriptionErrorEventArgs eArgs) {
				log.severe("re-open after disconnect...");
				try {
					if (!conn.getIsOpen()) {
						conn.open();
						log.info("connection re-opened.");
					}
				} catch (Exception e) {
					log.severe("error re-opening connection: " + e.getMessage());
					try {
						log.severe("re-start monitoring...");
						monitorInbox();
					} catch (Exception e1) {
						log.severe("error re-opening connection: " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}});
		conn.open();
		log.info("connection opened first time.");
	}

}

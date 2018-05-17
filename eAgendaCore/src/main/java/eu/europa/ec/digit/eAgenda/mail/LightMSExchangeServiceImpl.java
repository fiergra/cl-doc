package eu.europa.ec.digit.eAgenda.mail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.availability.FreeBusyViewType;
import microsoft.exchange.webservices.data.core.enumeration.availability.SuggestionQuality;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
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
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.AvailabilityOptions;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.AttendeeCollection;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

public class LightMSExchangeServiceImpl implements ILightMSExchangeService {

	public static Logger log = Logger.getLogger(LightMSExchangeServiceImpl.class.getName());

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
	private ExchangeService service;

	private String fmb = null;

	private Mailbox mailBox;

	
	private synchronized void setExchangeService(String keyUser, String keyPass, String fmb) throws Exception {
		service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
		service.setExchange2007CompatibilityMode(true);
		ExchangeCredentials credentials = new WebCredentials(keyUser, keyPass);
		service.setCredentials(credentials);
		service.autodiscoverUrl(fmb);
		mailBox = new Mailbox(fmb);
		setFmb(fmb);
		initialized = true;
	}

	public LightMSExchangeServiceImpl() {

	}

	public LightMSExchangeServiceImpl(String exchangeUser, String exchangePass, String fmb) throws Exception {
		init(exchangeUser, exchangePass, fmb);
	}

	private void init(String keyUser, String keyPass, String fmb) throws Exception {
		if (!isInitialized()) {
			setExchangeService(keyUser, keyPass, fmb);
		}
	}

	

	@Override
	public synchronized void removeAppointmentFromOutlookCalendar(final String id) throws Exception {

		Appointment ooApt = getExistingOutlookAppointment(id);
		if (ooApt != null) {
			ooApt.delete(DeleteMode.MoveToDeletedItems, SendCancellationsMode.SendOnlyToAll);
		}
	}

	private Appointment getExistingOutlookAppointment(String id) {
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
	public boolean addAppointmentIntoOutlookCalendar(String[] recipients, String subject, String location, String message, eu.europa.ec.digit.eAgenda.Appointment outlookAppointment) throws Exception {
		boolean createdNew = false;

		synchronized (service) {
				String appointmentId = outlookAppointment.id.toHexString();
				Date dateFrom = outlookAppointment.from;
				Date dateTo = outlookAppointment.until;
				String place = outlookAppointment.location != null ? outlookAppointment.location.getDisplayName() : null;
				FolderId fid1 = new FolderId(WellKnownFolderName.Calendar, mailBox);

				Appointment existingAppointment = getExistingOutlookAppointment(appointmentId);
//				byte[] fileContent = outlookAppointment.getAttachment();
//				String fileName = outlookAppointment.getAttachmentFileName();

				if (existingAppointment == null) {
					Appointment calAppointment = new Appointment(service);
					ExtendedPropertyDefinition extPropDef = new ExtendedPropertyDefinition(_UUID, _CUSTOM_PROPERTY_GEN_ITEM, MapiPropertyType.String);
					calAppointment.setExtendedProperty(extPropDef, appointmentId);
					calAppointment.setInReplyTo(appointmentId.toString());
					calAppointment.setAllowNewTimeProposal(false);
					calAppointment.setIsResponseRequested(true);
					calAppointment.setSubject(subject);
					calAppointment.setBody(MessageBody.getMessageBodyFromText(message));
					calAppointment.setImportance(Importance.High);
					calAppointment.setStart(dateFrom);
					calAppointment.setEnd(dateTo);
					calAppointment.setIsReminderSet(true);
					calAppointment.setLocation(place);
					calAppointment.setReminderMinutesBeforeStart(30);
					calAppointment.setSensitivity(Sensitivity.Private);
//					if (fileContent != null) {
//						calAppointment.getAttachments().addFileAttachment(fileName, fileContent);
//					}
					calAppointment.save(fid1, SendInvitationsMode.SendToNone);

					for (String email : recipients) {
						calAppointment.getRequiredAttendees().add(email);
					}
					calAppointment.update(ConflictResolutionMode.AutoResolve, SendInvitationsOrCancellationsMode.SendToChangedAndSaveCopy);
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

				mail.setFrom(new EmailAddress(fmb));
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

	public String getFmb() {
		return fmb;
	}

	public void setFmb(String fmb) {
		this.fmb = fmb;
	}

	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public List<AttendeeAvailability> getFreeBusyInfo(List<String> addresses, Date startDate, Date endDate) throws Exception {
		synchronized (service) {
			// Create a collection of attendees.
			List<AttendeeInfo> attendees = new ArrayList<AttendeeInfo>();
			for (String a:addresses) {
				attendees.add(AttendeeInfo.getAttendeeInfoFromString(a));
			}
	
			// Specify options to request free/busy information and suggested meeting times.
			AvailabilityOptions availabilityOptions = new AvailabilityOptions();
			availabilityOptions.setGoodSuggestionThreshold(49);
			availabilityOptions.setMaximumNonWorkHoursSuggestionsPerDay(0);
			availabilityOptions.setMaximumSuggestionsPerDay(2);
			// Note that 60 minutes is the default value for MeetingDuration, but setting it
			// explicitly for demonstration purposes.
			availabilityOptions.setMeetingDuration(30);
			availabilityOptions.setMinimumSuggestionQuality(SuggestionQuality.Good);
			availabilityOptions.setDetailedSuggestionsWindow(new TimeWindow(startDate, endDate));
			availabilityOptions.setRequestedFreeBusyView(FreeBusyViewType.Detailed);
	
			// Return free/busy information and a set of suggested meeting times.
			// This method results in a GetUserAvailabilityRequest call to EWS.
			GetUserAvailabilityResults results = service.getUserAvailability(attendees, availabilityOptions.getDetailedSuggestionsWindow(), AvailabilityData.FreeBusy, availabilityOptions);
			List <AttendeeAvailability> availabilities = new ArrayList<>();
			for (AttendeeAvailability availability : results.getAttendeesAvailability()) {
				availabilities.add(availability);
			}
			return availabilities;
		}
	}

}
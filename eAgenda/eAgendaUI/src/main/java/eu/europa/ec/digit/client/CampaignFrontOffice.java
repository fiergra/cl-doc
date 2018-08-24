package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ceres.dynamicforms.client.ClientDateHelper;
import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.ceres.dynamicforms.client.components.ObjectSelectorComboBox;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Day;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Slot;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class CampaignFrontOffice extends DockLayoutPanel {
	
	private static final String STYLE_DISABLED = "slotDisabled";
	private static final String STYLE_PLACES_AVAILABLE = "placesAvailable";
	private static final String STYLE_COMPLETE = "complete";
	
	private WorkPatternHelper wpHelper = new WorkPatternHelper();
	private List<Appointment> personalAppointments;
	private List<Widget> personalAppointmentRenderers = new ArrayList<>();
	private VerticalPanel vpSlots = new VerticalPanel();

	private final ApplicationHeader ah;

	private DatePicker datePicker = new DatePicker();
	private IResource host = null;
	private User guest = eAgendaUI.userContext.user;
	private Campaign campaign;

	private UpdateWebSocketClient wsClient = new UpdateWebSocketClient();
	private List<String> holidays;
	
	public CampaignFrontOffice(Campaign campaign, List<String> holidays) {
		super(Unit.PX);
		ah = new ApplicationHeader(eAgendaUI.userContext, campaign.name);
		this.campaign = campaign;
		this.holidays = holidays;
		
		Window.setTitle("eAgenda: " + campaign.name);
		
		Label lbAgendaName = new I18NLabel(campaign.name);
		lbAgendaName.setStyleName("frontOfficeCampaignName");

		if (campaign.allowDelegation) {
			ah.addUserClickHandler(e -> impersonate());
		}
		
		if (!campaign.published) {
			Label lbTopSecret = new I18NLabel("NOT PUBLISHED!");
			lbTopSecret.setStyleName("notPublished");
			ah.hpLeft.add(lbTopSecret);
		}
		addNorth(ah, 54);
		
		FlexTable ftMain = new FlexTable();
		VerticalPanel vpMain = new VerticalPanel();
		ftMain.setWidget(0, 0, vpMain);
		vpMain.setSpacing(3);
		add(ftMain);
		
		if (campaign.patterns != null) {
			List<IResource> resources = campaign.assignedResources();
			
			resources = resources.stream().filter(r -> {
				List<WorkPattern> resourcePatterns = campaign.resourcePatterns(r);
				return resourcePatterns != null && resourcePatterns.stream().anyMatch(p -> (p.days != null && p.days.stream().anyMatch(d -> d.slots != null && !d.slots.isEmpty())));
			}).collect(Collectors.toList());
			
			datePicker.addShowRangeHandlerAndFire(e -> {
				Date now = ClientDateHelper.trunc(new Date());
				Date minDate = wpHelper.getMinDate();
				
				if (minDate == null || minDate.getTime() < now.getTime()) {
					minDate = now;
				}
				
				long curr = ClientDateHelper.trunc(e.getStart()).getTime();
				while (curr < minDate.getTime() && curr <= e.getEnd().getTime()) {
					datePicker.setTransientEnabledOnDates(false, new Date(curr));
					curr += ClientDateHelper.DAY_MS;
				}
				
				if (wpHelper.getMaxDate() != null) {
					curr = wpHelper.getMaxDate().getTime() + ClientDateHelper.DAY_MS;
					
					if (curr <= e.getStart().getTime()) {
						curr = e.getStart().getTime();
					}
					
					while (curr < e.getEnd().getTime()) {
						datePicker.setTransientEnabledOnDates(false, new Date(curr));
						curr += ClientDateHelper.DAY_MS;
					}
				}
				
				if (host != null) {
					loadAppointmentsAndCheckAvailability(host);
				}
			});
			
			datePicker.addValueChangeHandler(e->setSelectedDate(datePicker.getValue()));
			
			ObjectSelectorComboBox<IResource> cmbResources = new ObjectSelectorComboBox<IResource>(resources.size() > 1) {

				@Override
				protected String labelFunc(IResource r) {
					return r != null ? r.getDisplayName() : StringResources.getLabel("<select your host>");
				}
				
			};
			cmbResources.setWidth("100%");
			cmbResources.populate(resources);
			cmbResources.addChangeHandler(e -> setSelectedResource(campaign, datePicker, cmbResources.getValue()));
			vpMain.add(cmbResources);
			datePicker.setVisible(false);
			vpMain.add(datePicker);
			
			vpMain.setWidth("20%");
			vpSlots.setWidth("100%");
//			hpMain.add(vpSlots);
			ftMain.setWidget(0, 1, vpSlots);
			ftMain.getFlexCellFormatter().setWidth(0, 1, "100%");
			ftMain.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
			ftMain.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

			if (resources.size() == 1) {
				setSelectedResource(campaign, datePicker, resources.get(0));
			}
		}
		
		loadAndDisplayPersonalAppointments();
		
	}

	
	
	private void impersonate() {
		HorizontalPanel hpImpersonate = new HorizontalPanel();
		hpImpersonate.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpImpersonate.setSpacing(5);
		
		PopupPanel pup = new PopupPanel(true);
		PersonSearchBox psb = new PersonSearchBox();
		psb.addSelectionHandler(s -> {
			doImpersonate(pup, psb);
		});
		
		hpImpersonate.add(new I18NLabel("type the name of a user you want to impersonate"));
		hpImpersonate.add(psb);
		PushButton pb = new PushButton(new Image("assets/images/24x24/cancel.png"), (ClickHandler)e -> pup.hide());
		pb.setStyleName("blankButton");
		hpImpersonate.add(pb);
		
		pup.add(hpImpersonate);
		pup.center();
		psb.setFocus(true);
	}



	private void doImpersonate(PopupPanel pup, PersonSearchBox psb) {
		if (psb.getSelected() == null) {
			guest = eAgendaUI.userContext.user;
		} else {
			guest = psb.getSelected();
		}

		pup.hide();
		
		loadAndDisplayPersonalAppointments();
		
		if (datePicker.getValue() != null) {
			setSelectedDate(datePicker.getValue());
		}
		
		if (guest.equals(eAgendaUI.userContext.user)) {
			ah.hiliteUser(false);
			ah.lbUserName.setTitle(StringResources.getLabel("connected as '") + eAgendaUI.userContext.user.userId);
		} else {
			ah.hiliteUser(true);
			ah.lbUserName.setTitle(StringResources.getLabel("connected as '") + eAgendaUI.userContext.user.userId + StringResources.getLabel("' but impersonating '") + guest.userId + "'");
		}
	}



	private void loadAndDisplayPersonalAppointments() {
		Date today = ClientDateHelper.trunc(new Date()); 
		eAgendaUI.service.getAppointments(today, ClientDateHelper.addDays(today, 90), null, guest, new RPCCallback<List<Appointment>>() {

			@Override
			protected void onResult(List<Appointment> result) {
				personalAppointments = result;
				displayPersonalAppointments();
			}
		});
	}

	
	private void displayPersonalAppointments() {
		RootLayoutPanel rlp = RootLayoutPanel.get();
		
		personalAppointmentRenderers.forEach(r -> rlp.remove(r));
		personalAppointmentRenderers.clear();
		
		if (personalAppointments != null && !personalAppointments.isEmpty()) {
			int offset = 0;
			int startX = personalAppointments.size() * 10;
			
			for (Appointment a:personalAppointments) {
				PostItAppointmentRenderer piar = new PostItAppointmentRenderer(a);
				personalAppointmentRenderers.add(piar);
				piar.setCancelHandler(e -> cancelAppointment(a));
				rlp.add(piar);
				rlp.setWidgetTopHeight(piar, 40 + offset, Unit.PX, 300, Unit.PX);
				rlp.setWidgetRightWidth(piar, startX - offset, Unit.PX, 300, Unit.PX);
				offset += 20;
			}
		}
	}

	private void setSelectedDate(Date d) {
		WorkPattern pattern = wpHelper.getPatternForDay(d);
		
		vpSlots.clear();
		vpSlots.add(createSlotsHeader(pattern, d));
		if (pattern != null) {
			Day day = pattern.getDay(d);
			if (day != null && day.slots != null) {
				
				List<Slot> combinedSlots = getCombinedSlots(day.slots, campaign.appointmentType.duration);
				
				Slot prevSlot = null;
				for (Slot slot:combinedSlots) {
					boolean placesAvailable = false;

					Panel slotRenderer;
					if (slot.capacity == null || slot.capacity == 0) {
						slotRenderer = createSlotRenderer(prevSlot != null && prevSlot.h == slot.h, pattern, slot, false, null);
						slotRenderer.setStyleName(STYLE_DISABLED);
					} else { 
						List<Appointment> appointmentsInSlot = wpHelper.getAppointmentsInSlot(wsClient.getAppointments(), d, slot); //wsClient.getAppointments().parallelStream().filter(a -> inSlot(a, d, slot)).collect(Collectors.toList());
						placesAvailable = placesAvailable || appointmentsInSlot.size() < slot.capacity;
						
						String content = "";
//						if (containsMyAppointment(appointmentsInSlot)) {
//							Date sd = slot.getFrom(d);
//							content = "</img src=\"assets/images/add.png\">" + ClientDateFormatter.dtfMonth.format(sd) + " " + ClientDateFormatter.dtfTime.format(sd) + "@" + host.getDisplayName();
//						}

						slotRenderer = createSlotRenderer(prevSlot != null && prevSlot.h == slot.h, pattern, slot, placesAvailable, content);
						slotRenderer.setStyleName(placesAvailable ? STYLE_PLACES_AVAILABLE : STYLE_COMPLETE);
					}
					vpSlots.add(slotRenderer);
					prevSlot = slot;
				}
				
				
			}
		}
	}

	private List<Slot> getCombinedSlots(List<Slot> slots, int duration) {
		List<Slot> combinedSlots = new ArrayList<>();
		if (slots != null) {
			for (int i = 0; i < slots.size(); i++) {
				Slot combined = new Slot(slots.get(i));
				combinedSlots.add(combined);
				int j = i + 1;
				while (combined.durationInMinutes < duration && j < slots.size() && combined.isAdjacent(slots.get(j))) {
					combined.combineWith(slots.get(j++));
				}

			}
		}
		return combinedSlots;
	}



	private Widget createSlotsHeader(WorkPattern pattern, Date d) {
		VerticalPanel vpHeader = new VerticalPanel();
		// sunday is zero
		int dayOfWeek = ClientDateHelper.getDayOfWeek(d) - 1;
		Label lbDayOfWeek = new I18NLabel(WorkPatternEditor.dayNames[dayOfWeek]);
		lbDayOfWeek.setStyleName("slotsHeaderDayName");
		Label lbDate = new Label(ClientDateFormatter.dtfMonth.format(d));
		lbDate.setStyleName("slotsHeaderDayName");

		HorizontalPanel hpDay = new HorizontalPanel();
		hpDay.setSpacing(5);
		hpDay.add(lbDayOfWeek);
		hpDay.add(lbDate);
		
		vpHeader.add(hpDay);
		
		if (pattern.location != null) {
			Label lbLocation = new Label(pattern.location.getDisplayName());
			lbLocation.setStyleName("slotsHeaderLocation");
			vpHeader.add(lbLocation);
		}
		return vpHeader;
	}

//	private boolean containsMyAppointment(List<Appointment> appointmentsInSlot) {
//		return appointmentsInSlot.stream().anyMatch(a -> a.guest != null && a.guest.equals(guest));
//	}

	private Panel createSlotRenderer(boolean sameHour, WorkPattern pattern, Slot slot, boolean enabled, String content) {
		HorizontalPanel hpSlot = new HorizontalPanel();
		hpSlot.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		hpSlot.setWidth("100%");
		
		Label lbHour = new Label();
		lbHour.setStyleName("slotHourLabel");
		if (!sameHour) {
			lbHour.setText(String.valueOf(slot.h));
		}
		Label lbMinute = new Label(String.valueOf(slot.m));
		lbMinute.setStyleName("slotMinuteLabel");
		
		HTML theSlot = new HTML(content);
		theSlot.setStyleName("theSlot");
		
		if (enabled) {
			theSlot.addClickHandler(c->saveAppointment(pattern, slot));
		}
		
		hpSlot.add(lbHour);
		hpSlot.add(lbMinute);
		hpSlot.add(theSlot);
		
		return hpSlot;
	}

	private void saveAppointment(WorkPattern pattern, Slot slot) {
	
		if (personalAppointments != null && !personalAppointments.isEmpty()) {
			MessageBox.show(StringResources.getLabel("Replace existing appointments"), StringResources.getLabel("Do you want to replace the upcoming appointments?"), MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION, r -> { 
				if (r.equals(MessageBox.MB_YES)) {
					personalAppointments.forEach(a -> cancelAppointment(a));
					personalAppointments.clear();
					doSaveAppointment(pattern, slot);
				} 
			});
		} else {
			doSaveAppointment(pattern, slot);
		}
	}
	
	private void cancelAppointment(Appointment a) {
		eAgendaUI.service.cancelAppointment(a, new RPCCallback.NOP<Appointment>());
	}

	
	private void doSaveAppointment(WorkPattern pattern, Slot slot) {
		Date from = slot.getFrom(datePicker.getValue());
		Date until = campaign.appointmentType.duration > 0 ? new Date(from.getTime() + campaign.appointmentType.duration * 60L * 1000L) : slot.getUntil(datePicker.getValue());
		Appointment a = new Appointment(host, guest, pattern.location, from, until, campaign.appointmentType);
		eAgendaUI.service.saveAppointment(campaign, a, new RPCCallback<Appointment>() {

			@Override
			protected void onResult(Appointment a) {
//				appointments.add(a);
//				personalAppointments.add(a);
			}
		});
		
		
	}


	private void setSelectedResource(Campaign campaign, DatePicker datePicker, IResource iResource) {
		host = iResource;
		if (iResource == null) {
			datePicker.setVisible(false);
			vpSlots.clear();
		} else {
			datePicker.setVisible(true);
			wpHelper.setPatterns(campaign.startDelayInH, campaign.resourcePatterns(iResource));
			Date preferredDate = wpHelper.getPreferredDate(datePicker.getValue()); 
	
			datePicker.setValue(preferredDate);
			datePicker.setCurrentMonth(preferredDate);
			
			wsClient.unsubscribe();
			wsClient.subscribe(eAgendaUI.userContext.user, host, null, (t, a) -> {
				switch (t) {
				case update:break;
				case insert:
					if (a.guest.equals(guest)) {
						personalAppointments.add(a);
						displayPersonalAppointments();
					}
					checkAvailability();
					setSelectedDate(datePicker.getValue());
					break;
				case delete:
					checkAvailability();
					setSelectedDate(datePicker.getValue());
					if (personalAppointments.remove(a)) {
						displayPersonalAppointments();
					}
					break;
				default: loadAppointmentsAndCheckAvailability(iResource); 
				}
				
			});
	
			loadAppointmentsAndCheckAvailability(iResource);
		}
	}

	private void loadAppointmentsAndCheckAvailability(IResource iResource) {
		AsyncCallback<List<Appointment>> callback = new RPCCallback<List<Appointment>>() {

			@Override
			protected void onResult(List<Appointment> result) {
				wsClient.setAppointments(result);
				checkAvailability();
				setSelectedDate(datePicker.getValue());
			}
		};
		eAgendaUI.service.getAppointments(ClientDateHelper.trunc(datePicker.getFirstDate()), ClientDateHelper.trunc(ClientDateHelper.addDays(datePicker.getLastDate(), 1)), iResource, null, callback);
	}

	private void checkAvailability() {
		Date dCurr = datePicker.getFirstDate();
		long curr = dCurr.getTime();
		long end = ClientDateHelper.trunc(new Date(datePicker.getLastDate().getTime() + ClientDateHelper.DAY_MS)).getTime();
		
		while (curr < end) {
			Date d = new Date(curr);
			
			if (isHoliday(d)) {
				datePicker.addStyleToDates(STYLE_COMPLETE, d);
			} else {
				WorkPattern pattern = wpHelper.getPatternForDay(d);
				if (pattern != null) {
					Day day = pattern.getDay(d);
					boolean placesAvailable = false;
					if (day != null && day.slots != null) {
						for (Slot slot:day.slots) {
							if (slot.capacity == null || slot.capacity == 0) {
								// disabled
							} else { 
								List<Appointment> appointmentsInSlot =  wpHelper.getAppointmentsInSlot(wsClient.getAppointments(), d, slot);//.parallelStream().filter(a -> inSlot(a, d, slot)).collect(Collectors.toList());
								placesAvailable = placesAvailable || appointmentsInSlot.size() < slot.capacity;
							}
						}
						if (datePicker.isDateEnabled(d)) {
							datePicker.removeStyleFromDates(STYLE_COMPLETE, d);
							datePicker.removeStyleFromDates(STYLE_PLACES_AVAILABLE, d);
							datePicker.addTransientStyleToDates(placesAvailable ? STYLE_PLACES_AVAILABLE : STYLE_COMPLETE, d);
						}
					}
				}
			}
			curr += ClientDateHelper.DAY_MS;
		}
	}



	private boolean isHoliday(Date d) {
		return holidays.contains(ClientDateHelper.df.format(d));
	}

	
	
	
}

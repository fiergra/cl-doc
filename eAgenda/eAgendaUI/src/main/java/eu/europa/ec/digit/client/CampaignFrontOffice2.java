package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Day;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Person;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.Slot;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class CampaignFrontOffice2 extends DockLayoutPanel {

	private static final String STYLE_DISABLED = "slotDisabled";
	private static final String STYLE_PLACES_AVAILABLE = "placesAvailable";
	private static final String STYLE_COMPLETE = "complete";

	private WorkPatternHelper wpHelper = new WorkPatternHelper();
	private List<Appointment> personalAppointments;
	private List<Widget> personalAppointmentRenderers = new ArrayList<>();
	private VerticalPanel vpSlots = new VerticalPanel();
	private VerticalPanel vpResourceAndCalendar = new VerticalPanel();

	private final ApplicationHeader ah;

	private DatePicker datePicker = new DatePicker();
	private IResource host = null;
	private User guest = eAgendaUI.userContext.user;
	private Campaign campaign;

	private UpdateWebSocketClient wsClient = new UpdateWebSocketClient();
	private Collection<Date> holidays;

	public CampaignFrontOffice2(Campaign campaign, Collection<Date> holidays) {
		super(Unit.PX);
		ah = new ApplicationHeader(eAgendaUI.userContext, campaign.name);
		this.campaign = campaign;
		this.holidays = holidays;

		Window.setTitle("eAgenda: " + campaign.name);

		Label lbAgendaName = new I18NLabel(campaign.name);
		lbAgendaName.setStyleName("frontOfficeCampaignName");

		if (campaign.allowDelegation) {
			HTML clickHere = new HTML(getClickHereText(eAgendaUI.userContext.user));
			clickHere.addStyleName("dynamicHyperLink");
			clickHere.setHeight("100%");
			clickHere.addClickHandler(e -> impersonate(clickHere));
			ah.hpRight.add(clickHere);
		}
		if (!campaign.published) {
			Label lbTopSecret = new I18NLabel("NOT PUBLISHED!");
			lbTopSecret.setStyleName("notPublished");
			ah.hpLeft.add(lbTopSecret);
		}
		addNorth(ah, 54);

		FlexTable ftMain = new FlexTable();
		ftMain.setStyleName("ftMain");
		vpResourceAndCalendar.setStyleName("city");
		ftMain.setWidget(0, 0, vpResourceAndCalendar);
//		vpMain.setSpacing(3);
		
		
		add(new ScrollPanel(ftMain));

		if (campaign.patterns != null) {
			List<IResource> resources = campaign.assignedResources();

			resources = resources.stream().filter(r -> {
				List<WorkPattern> resourcePatterns = campaign.resourcePatterns(r);
				return resourcePatterns != null && resourcePatterns.stream().anyMatch(
						p -> (p.days != null && p.days.stream().anyMatch(d -> d.slots != null && !d.slots.isEmpty())));
			}).collect(Collectors.toList());

			datePicker.addShowRangeHandlerAndFire(e -> {
				if (host == null) {
					disableDatePicker();
				} else {
					Date now = ClientDateHelper.trunc(new Date());
					Date minDate = wpHelper.getMinDate();

					if (minDate == null || minDate.getTime() < now.getTime()) {
						minDate = now;
					}

					Date start = e.getStart();
					long curr = ClientDateHelper.trunc(start).getTime();
					while (curr < minDate.getTime() && curr <= e.getEnd().getTime()) {
						Date currDate = new Date(curr);
						datePicker.setTransientEnabledOnDates(false, currDate);
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

					loadAppointmentsAndCheckAvailability(host);
				}
			});

			datePicker.addValueChangeHandler(e -> setSelectedDate(datePicker.getValue()));

			ObjectSelectorComboBox<IResource> cmbResources = new ObjectSelectorComboBox<IResource>(
					resources.size() > 1) {

				@Override
				protected String labelFunc(IResource r) {
					return r != null ? r.getDisplayName() : getLabel("<select>");
				}

				private String getLabel(String string) {
					String label = StringResources.getLabel("<select>");
					
					boolean hasPerson = entities.stream().anyMatch(e -> e instanceof Person);
					boolean hasUser = entities.stream().anyMatch(e -> e instanceof User);
					boolean hasRoom = entities.stream().anyMatch(e -> e instanceof Room);
					
					if ((hasPerson || hasUser) && hasRoom) {
						label = StringResources.getLabel("<select host or location>");
					} else if (hasRoom) {
						label = StringResources.getLabel("<select location>");
					} else if (hasPerson || hasUser) {
						label = StringResources.getLabel("<select host>");
					}
					
					return label;
				}

			};
			cmbResources.setWidth("100%");
			cmbResources.setStyleName("slotsHeaderDayName");
			cmbResources.addStyleName("slotSelectionHeader");
			cmbResources.populate(resources);
			cmbResources.addChangeHandler(e -> setSelectedResource(campaign, datePicker, cmbResources.getValue()));
			vpResourceAndCalendar.add(cmbResources);
			vpResourceAndCalendar.add(datePicker);

			vpResourceAndCalendar.add(createInstructions());

			vpResourceAndCalendar.setWidth("20%");
			vpSlots.setStyleName("dailySlotsPanel");
			
			ftMain.setWidget(0, 1, vpSlots);
			ftMain.setHeight("100%");
			ftMain.getFlexCellFormatter().setWidth(0, 1, "100%");
			ftMain.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
			ftMain.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

			if (resources.size() == 1) {
				setSelectedResource(campaign, datePicker, resources.get(0));
			} else {
				subscribeToWebSocket(null);
			}
		}

		loadAndDisplayPersonalAppointments();

	}

	private Widget createInstructions() {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		Label heading = new I18NLabel("Instructions");
		heading.setStyleName("instructionsHeading");
		vp.add(heading);
		vp.add(createInstruction(1, "Select the location"));
		vp.add(createInstruction(2, "Chose a date"));
		vp.add(createInstruction(3, "Pick your preferred time"));
		
		return vp;
	}

	private Widget createInstruction(int i, String string) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Label number = new Label(String.valueOf(i));
		number.setStyleName("instructionsBullet");
		hp.add(number);
		Label text = new I18NLabel(string); 
		hp.add(text);
		
		return hp;
	}

	private String getClickHereText(User user) {
		return "Click <b>here</b> to fix appointments for </br>someone else than <b>" + getUserName(user) + "</b>";
	}

	private String getUserName(User user) {
		return user.person != null ? user.person.firstName + " " + user.person.lastName : user.userId;
	}

	private void disableDatePicker() {
		Date date = datePicker.getFirstDate();
		while (date.getTime() <= datePicker.getLastDate().getTime()) {
			datePicker.removeStyleFromDates(STYLE_PLACES_AVAILABLE, date);
			datePicker.removeStyleFromDates(STYLE_COMPLETE, date);
			datePicker.setTransientEnabledOnDates(false, date);
			date = ClientDateHelper.addDays(date, 1);
		}
	}

	private void impersonate(HTML clickHere) {
		HorizontalPanel hpImpersonate = new HorizontalPanel();
		hpImpersonate.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpImpersonate.setSpacing(5);

		PopupPanel pup = new PopupPanel(true);
		PersonSearchBox psb = new PersonSearchBox();
		psb.addSelectionHandler(s -> {
			doImpersonate(pup, psb, clickHere);
		});

		hpImpersonate.add(new I18NLabel("type the name of a user you want to impersonate"));
		hpImpersonate.add(psb);
		PushButton pb = new PushButton(new Image("assets/images/24x24/cancel.png"), (ClickHandler) e -> pup.hide());
		pb.setStyleName("blankButton");
		hpImpersonate.add(pb);

		pup.add(hpImpersonate);
		pup.center();
		psb.setFocus(true);
	}

	private void doImpersonate(PopupPanel pup, PersonSearchBox psb, HTML clickHere) {
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
		} else {
			ah.hiliteUser(true);
		}
		
		clickHere.setHTML(getClickHereText(guest));
		ah.setUserName(guest.person.firstName + " " + guest.person.lastName);
	}

	private void loadAndDisplayPersonalAppointments() {
		Date today = ClientDateHelper.trunc(new Date());
		eAgendaUI.service.getAppointments(today, ClientDateHelper.addDays(today, 90), null, guest, true,
				new RPCCallback<List<Appointment>>() {

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

			for (Appointment a : personalAppointments) {
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
		vpSlots.clear();
		if (d != null) {
			WorkPattern pattern = wpHelper.getPatternForDay(d);
			if (pattern != null) {
				vpSlots.add(createSlotsHeader(pattern, d));
				if (pattern != null) {
					Day day = pattern.getDay(d);
					if (day != null) {
						List<Slot> combinedSlots;
						if (day.slots == null) {
							combinedSlots = new ArrayList<>();
						} else {
							combinedSlots = getCombinedSlots(day.slots, campaign.appointmentType.duration);
						}
						combinedSlots = fillUp(pattern, combinedSlots); 
						
						Slot prevSlot = null;
						for (Slot slot : combinedSlots) {
							boolean placesAvailable = false;
		
							Panel slotRenderer;
							if (slot.capacity == null || slot.capacity == 0) {
								slotRenderer = createSlotRenderer(prevSlot != null && prevSlot.h == slot.h, pattern, slot, false, null);
								slotRenderer.setStyleName(STYLE_DISABLED);
								slotRenderer.setTitle(StringResources.getLabel("no places available at this time slot"));
							} else {
								List<Appointment> appointmentsInSlot = wpHelper
										.getAppointmentsInSlot(wsClient.getAppointments(), d, slot); // wsClient.getAppointments().parallelStream().filter(a
																										// -> inSlot(a, d,
																										// slot)).collect(Collectors.toList());
								placesAvailable = placesAvailable || appointmentsInSlot.size() < slot.capacity;
		
								String content = "";
		
								slotRenderer = createSlotRenderer(prevSlot != null && prevSlot.h == slot.h, pattern, slot,
										placesAvailable, content);
								
								if (placesAvailable) {
									slotRenderer.setStyleName(STYLE_PLACES_AVAILABLE);
									slotRenderer.setTitle(StringResources.getLabel("Click here to book this time slot"));
								} else {
									slotRenderer.setStyleName(STYLE_COMPLETE);
									slotRenderer.setTitle(StringResources.getLabel("Sorry, but this time slot is already booked"));
								}
							}
							vpSlots.add(slotRenderer);
							prevSlot = slot;
						}
		
					}
				}
			}
		}
	}

	private List<Slot> fillUp(WorkPattern pattern, List<Slot> slots) {
		List<Slot> filledUp = new ArrayList<>();
		if (slots.isEmpty()) {
			int h = 9, m = 0, time = h * 100 + m;
			while (time < 1700) {
				Slot s = new Slot(h, m, pattern.minuteGrid, null);
				filledUp.add(s);
				m += pattern.minuteGrid;
				if (m >= 60) {
					h++;
					m = 0;
				}
				time = h * 100 + m;
			}
		} else {
			int h = slots.get(0).h >= 12 ? 12 : slots.get(0).h < 9 ? slots.get(0).h : 9;
			int m = 0, time = h * 100 + m;
			int endH = slots.get(slots.size() - 1).h + 1;
			
			if (endH < 12) {
				endH = 12;
			}
			
			while (time < endH * 100) {
				Slot s = getSlot(slots, h, m, pattern.minuteGrid); 
				filledUp.add(s);
				m += pattern.minuteGrid;
				if (m >= 60) {
					h++;
					m = 0;
				}
				time = h * 100 + m;
			}
		}
		return filledUp;
	}

	private Slot getSlot(List<Slot> slots, int h, int m, int minuteGrid) {
		Optional<Slot> slot = slots.stream().filter(s -> s.h == h && s.m == m).findFirst();
		return slot.isPresent() ? slot.get() : new Slot(h, m, minuteGrid, null);
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
		vpHeader.setStyleName("slotSelectionHeader");
		// sunday is zero
		int dayOfWeek = ClientDateHelper.getDayOfWeek(d) - 1;
		Label lbDayOfWeek = new I18NLabel(WorkPatternEditor.dayNames[dayOfWeek]);
		lbDayOfWeek.setStyleName("slotsHeaderDayName");
		Label lbDate = new Label(ClientDateFormatter.dtfMonth.format(d));
		lbDate.setStyleName("slotsHeaderDayName");

		HorizontalPanel hpDay = new HorizontalPanel();
		hpDay.setSpacing(5);
		hpDay.setHeight("100%");
		hpDay.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
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

	private Panel createSlotRenderer(boolean sameHour, WorkPattern pattern, Slot slot, boolean enabled,	String content) {
		HorizontalPanel hpSlot = new HorizontalPanel();
		FocusPanel fpSlot = new FocusPanel(hpSlot);
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
			fpSlot.addClickHandler(c -> saveAppointment(pattern, slot));
		}

		hpSlot.add(lbHour);
		hpSlot.add(lbMinute);
		hpSlot.add(theSlot);

		return fpSlot;
	}

	private void saveAppointment(WorkPattern pattern, Slot slot) {

		if (personalAppointments != null && !personalAppointments.isEmpty()) {
			MessageBox.show(StringResources.getLabel("Replace existing appointments"),
					StringResources.getLabel("Do you want to replace the upcoming appointments?"), MessageBox.MB_YESNO,
					MESSAGE_ICONS.MB_ICON_QUESTION, r -> {
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
		Date from = slot.getFrom(ClientDateHelper.trunc(datePicker.getValue()));
		Date until = campaign.appointmentType.duration > 0
				? new Date(from.getTime() + campaign.appointmentType.duration * 60L * 1000L)
				: slot.getUntil(datePicker.getValue());
		Appointment a = new Appointment(campaign, host, guest, pattern.location, from, until, campaign.appointmentType);
		eAgendaUI.service.saveAppointment(campaign, a, new RPCCallback<Appointment>() {

			@Override
			protected void onResult(Appointment a) {
//				appointments.add(a);
//				personalAppointments.add(a);
			}
		});

	}
	
	private void subscribeToWebSocket(IResource iResource) {
		wsClient.unsubscribe();
		wsClient.subscribe(campaign, eAgendaUI.userContext.user, host, null, (t, a) -> {
			switch (t) {
			case update:
				break;
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
			default:
				loadAppointmentsAndCheckAvailability(iResource);
			}

		});
	}

	private void checkAndSet() {
		Date d = checkAvailability();
		datePicker.setValue(d);
		setSelectedDate(d);
	}

	private void setSelectedResource(Campaign campaign, DatePicker datePicker, IResource iResource) {
		host = iResource;
		if (iResource == null) {
			disableDatePicker();
			vpSlots.clear();
		} else {

			if (iResource instanceof Room) {
				vpResourceAndCalendar.setStyleName("city-" + ((Room) iResource).city.code.toLowerCase());
			} else {
				vpResourceAndCalendar.removeStyleName("city");
			}

			datePicker.setVisible(true);
			wpHelper.setPatterns(campaign.startDelayInH, campaign.resourcePatterns(iResource));
			Date preferredDate = wpHelper.getPreferredDate(datePicker.getValue());

			datePicker.setValue(preferredDate);
			datePicker.setCurrentMonth(preferredDate);
			
			subscribeToWebSocket(iResource);
			
			loadAppointmentsAndCheckAvailability(iResource);
		}
	}

	private void loadAppointmentsAndCheckAvailability(IResource iResource) {
		AsyncCallback<List<Appointment>> callback = new RPCCallback<List<Appointment>>() {

			@Override
			protected void onResult(List<Appointment> result) {
				wsClient.setAppointments(result);
				checkAndSet();
			}
		};
		eAgendaUI.service.getAppointments(ClientDateHelper.trunc(datePicker.getFirstDate()),
				ClientDateHelper.trunc(ClientDateHelper.addDays(datePicker.getLastDate(), 1)), iResource, null, false, callback);
	}

	private Date checkAvailability() {
		Date dCurr = datePicker.getFirstDate();
		Date minDate = new Date(new Date().getTime() + campaign.startDelayInH * 60L * 60L * 1000L);
		Date selectDate = null;
		
		long curr = dCurr.getTime();
		long end = ClientDateHelper.trunc(new Date(datePicker.getLastDate().getTime() + ClientDateHelper.DAY_MS)).getTime();

		while (curr < end) {
			Date d = new Date(curr);

			if (isHoliday(d)) {
				datePicker.addStyleToDates(STYLE_COMPLETE, d);
				datePicker.setTransientEnabledOnDates(false, d);
			} else {
				WorkPattern pattern = wpHelper.getPatternForDay(d);
				if (pattern != null) {
					Day day = pattern.getDay(d);
					boolean placesAvailable = false;
					if (day != null && day.slots != null) {
						for (Slot slot : day.slots) {
							if (slot.capacity == null || slot.capacity == 0) {
								// disabled
							} else {
								List<Appointment> appointmentsInSlot = wpHelper
										.getAppointmentsInSlot(wsClient.getAppointments(), d, slot);// .parallelStream().filter(a
																									// -> inSlot(a, d,
																									// slot)).collect(Collectors.toList());
								placesAvailable = placesAvailable || appointmentsInSlot.size() < slot.capacity;
							}
						}
						if (datePicker.isDateEnabled(d)) {
							datePicker.removeStyleFromDates(STYLE_COMPLETE, d);
							datePicker.removeStyleFromDates(STYLE_PLACES_AVAILABLE, d);
							datePicker.addTransientStyleToDates(placesAvailable ? STYLE_PLACES_AVAILABLE : STYLE_COMPLETE, d);
							
							if (selectDate == null && placesAvailable && d.getTime() >= minDate.getTime()) {
								selectDate = d;
							}
						}
					}
				}
			}
			curr += ClientDateHelper.DAY_MS;
		}
		
		return selectDate;
	}

	private boolean isHoliday(Date d) {
		return holidays.contains(ClientDateHelper.trunc(d));
	}

}

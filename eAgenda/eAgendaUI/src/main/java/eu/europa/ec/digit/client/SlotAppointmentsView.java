package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchBox;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Day;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Slot;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class SlotAppointmentsView extends DockLayoutPanel {

	private DateBox dateBox = new DateBox();
	private Campaign campaign;

	private IResource host = null;
	private List<WorkPattern> patterns;

	private FlexTable slotTable = new FlexTable();
	private final WorkPatternHelper wpHelper;

	private UpdateWebSocketClient wsClient = new UpdateWebSocketClient();

	public SlotAppointmentsView(Campaign campaign) {
		super(Unit.PX);
		this.campaign = campaign;
		wpHelper = new WorkPatternHelper();
		addNorth(createHeader(), 42);
		slotTable.addStyleName("slotTable");
		add(new ScrollPanel(slotTable));
	}

	private Widget createHeader() {
		HorizontalPanel hpHeader = new HorizontalPanel();
		hpHeader.setHeight("100%");
		hpHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpHeader.setSpacing(3);

		dateBox.setWidth("7em");
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd/MM/yyyy")));
		dateBox.getDatePicker().addShowRangeHandler(e -> {

			long curr = e.getStart().getTime();
			while (curr <= e.getEnd().getTime()) {
				Date dCurr = new Date(curr);
				dateBox.getDatePicker().setTransientEnabledOnDates(wpHelper.getPatternForDay(dCurr) != null, dCurr);
				curr += 24 * 60 * 60 * 1000;
			}

		});
		dateBox.setFireNullValues(true);
		dateBox.addValueChangeHandler(e -> showPatternAndAppointments(dateBox.getValue()));
		hpHeader.add(dateBox);

		return hpHeader;
	}

	public void setHost(IResource host) {
		this.host = host;
		patterns = campaign.resourcePatterns(host);

		wpHelper.setPatterns(campaign.startDelayInH, patterns);
		Date d = wpHelper.getPreferredDate(dateBox.getValue());
		dateBox.setValue(d, true);
		showPatternAndAppointments(d);

		wsClient.unsubscribe();
		wsClient.subscribe(eAgendaUI.userContext.user, host, d, (t, id) -> {
//			showPatternAndAppointments(dateBox.getValue());
			Date date = dateBox.getValue();
			WorkPattern pattern = wpHelper.getPatternForDay(date);
			if (pattern != null) {
				Day day = pattern.getDay(date);
				showAppointments(date, day, wsClient.getAppointments());
			}
		});

	}

	private void showPatternAndAppointments(Date d) {
		if (d != null) {
			WorkPattern pattern = wpHelper.getPatternForDay(d);

			slotTable.removeAllRows();
			appointmentPanels.clear();

			if (pattern != null) {
				Day day = pattern.getDay(d);
				if (day.slots != null && !day.slots.isEmpty()) {

					addHeader(d, day.slots);
					eAgendaUI.service.getAppointments(d, host, null, new RPCCallback<List<Appointment>>() {

						@Override
						protected void onResult(List<Appointment> appointments) {
							wsClient.setAppointments(appointments);
							showAppointments(d, day, appointments);
						}

					});

				}
			}
		}
	}

	private void showAppointments(Date d, Day day, List<Appointment> appointments) {
		appointmentPanels.forEach(p -> p.clear());
		if (day != null && day.slots != null) {
			for (int index = 0; index < day.slots.size(); index++) {
				if (index < appointmentPanels.size()) {
					Panel panel = appointmentPanels.get(index);
					List<Appointment> appointmentsInSlot = wpHelper.getAppointmentsInSlot(wsClient.getAppointments(), d,
							day.slots.get(index));
					appointmentsInSlot.forEach(a -> panel.add(createAppointmentRenderer(a, panel)));
				}
			}
			;
		}
	}

	List<Panel> appointmentPanels = new ArrayList<>();

	private boolean horizontal = false;
	
	private void addHeader(Date d, List<Slot> slots) {
		int appointmentsRow = 0;

		if (horizontal) {
			int column = 0;
			for (Slot slot : slots) {
				int row = 0;
				slotTable.setWidget(row++, column, createSlotHeader(slot, column));
				VerticalPanel vpAppointments = new VerticalPanel();
				vpAppointments.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				vpAppointments.setStyleName("slotAppointmentPanel");
				appointmentPanels.add(vpAppointments);
				appointmentsRow = row;
				slotTable.setWidget(row++, column, vpAppointments);
				column++;
			}
			slotTable.getRowFormatter().addStyleName(appointmentsRow, "slotAppointmentRow");
		} else {
			int row = 0;
			for (Slot slot : slots) {
				int column = 0;
				slotTable.setWidget(row, column++, createSlotHeader(slot, row));
				HorizontalPanel vpAppointments = new HorizontalPanel();
				vpAppointments.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				vpAppointments.setStyleName("slotAppointmentPanel");
				appointmentPanels.add(vpAppointments);
				slotTable.setWidget(row, column++, vpAppointments);
				slotTable.getRowFormatter().addStyleName(row, "slotAppointmentRow");
				row++;
			}
		}

	}

	private Widget createAppointmentRenderer(Appointment a, Panel panel) {
		AppointmentRenderer ar = new AppointmentRenderer(a);
		ar.setOnDelete(() -> {
			eAgendaUI.service.cancelAppointment(a, new RPCCallback<Appointment>() {

				@Override
				protected void onResult(Appointment result) {
					panel.remove(ar);
				}
			});
		});
		return ar;
	}

	private RunSearch<User> runSearch = new RunSearch<User>() {

		@Override
		public void run(Request request, Callback callback, LabelFunc<User> replacement, LabelFunc<User> display) {
			eAgendaUI.service.findPersons(request.getQuery(), new RPCCallback<List<User>>() {

				@Override
				protected void onResult(List<User> resources) {
					Collection<Suggestion> suggestions = new ArrayList<SuggestOracle.Suggestion>();
					for (User p : resources) {
						MultiWordSuggestion suggestion = new SearchSuggestion<IResource>(p, replacement.label(p),
								display.label(p));
						suggestions.add(suggestion);
					}
					Response response = new Response(suggestions);
					callback.onSuggestionsReady(request, response);

				}
			});
		}
	};

	private LabelFunc<User> lf = r -> r.getDisplayName();

	private void createAppointment(int column, Slot slot, SearchBox<User> searchBox) {
		User guest = searchBox.getSelected();
		if (guest != null) {
			SimpleForm sf = new SimpleForm();
			sf.setSize("100%", "100%");
			sf.addLine("Guest", new I18NLabel(guest.getDisplayName()));
			TextArea txtComment = new TextArea();
			txtComment.setSize("100%", "100%");
			sf.addLine("Comment", txtComment);
			MessageBox.show("Create Appointment", sf, MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION, i -> {

				if (i == MessageBox.MB_YES) {

					Date d = dateBox.getValue();
					Date from = slot.getFrom(d);
					Date until = new Date(from.getTime() + campaign.appointmentType.duration * 60 * 1000L);
					Appointment a = new Appointment(host, guest, null, from, until, campaign.appointmentType);
					a.comment = txtComment.getText();
					eAgendaUI.service.saveAppointment(campaign, a, new RPCCallback<Appointment>() {

						@Override
						protected void onResult(Appointment result) {
//							Panel panel = appointmentPanels.get(column);
//							Widget renderer = createAppointmentRenderer(result, panel);
//							panel.add(renderer);

							Scheduler.get().scheduleDeferred(() -> {
								searchBox.setSelected(null);
								searchBox.setFocus(true);
							});
						}
					});
				}
			}, 600, 400);
		}
	}

	private String prefix(int i) {
		return i < 10 ? "0" + i : String.valueOf(i);
	}

	private Widget createSlotHeader(Slot slot, int column) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSpacing(3);
		Label l = new I18NLabel(prefix(slot.h) + ":" + prefix(slot.m));

		RemoteSearchBox<User> searchBox = new RemoteSearchBox<>(new SimpleTranslator<User>(), runSearch, lf, lf);
		searchBox.addSelectionHandler(r -> createAppointment(column, slot, searchBox));
		searchBox.setWidth("18em");

		hp.add(l);
		hp.add(searchBox);

		return hp;
	}

}

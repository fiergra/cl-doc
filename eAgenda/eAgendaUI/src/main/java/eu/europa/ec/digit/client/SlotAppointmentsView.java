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
	private WorkPatternHelper wpHelper = new WorkPatternHelper();
	
	private UpdateWebSocketClient wsClient = new UpdateWebSocketClient();
	
	public SlotAppointmentsView(Campaign campaign) {
		super(Unit.PX);
		this.campaign = campaign;
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

			
//			if (wpHelper.getMinDate() != null) {
//				long curr = e.getStart().getTime();
//				while (curr < wpHelper.getMinDate().getTime() && curr <= e.getEnd().getTime()) {
//					dateBox.getDatePicker().setTransientEnabledOnDates(false, new Date(curr));
//					curr += 24 * 60 * 60 * 1000;
//				}
//			}
//			
//			if (wpHelper.getMaxDate() != null) {
//				long curr = wpHelper.getMaxDate().getTime() + 24 * 60 * 60 * 1000;
//				
//				if (curr <= e.getStart().getTime()) {
//					curr = e.getStart().getTime();
//				}
//				
//				while (curr < e.getEnd().getTime()) {
//					dateBox.getDatePicker().setTransientEnabledOnDates(false, new Date(curr));
//					curr += 24 * 60 * 60 * 1000;
//				}
//			}
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
		wsClient.subscribe(host, d, (t, id) -> { 
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

					} );
					
				}
			}
		}
	}

	private void showAppointments(Date d, Day day, List<Appointment> appointments) {
		appointmentPanels.forEach(p -> p.clear());
		if (day != null && day.slots != null) {
			for (int index = 0; index < day.slots.size(); index++) {
				Panel panel = appointmentPanels.get(index);
				List<Appointment> appointmentsInSlot = wpHelper.getAppointmentsInSlot(wsClient.getAppointments(), d, day.slots.get(index));
				appointmentsInSlot.forEach(a -> panel.add(createAppointmentRenderer(a, panel)));
			};
		}		
	}

	List<Panel> appointmentPanels = new ArrayList<>();
	
	private void addHeader(Date d, List<Slot> slots) {
		int column = 0;
		int appointmentsRow = 0;
		
		for (Slot slot:slots) {
			int row = 0;
			slotTable.setWidget(row++, column, createSlotHeader(slot, column));
//			slotTable.setWidget(row++, column, createPersonSearchBox(column, slot));
			VerticalPanel vpAppointments = new VerticalPanel();
			vpAppointments.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
//			vpAppointments.setSpacing(3);
			vpAppointments.setStyleName("slotAppointmentPanel");
			appointmentPanels.add(vpAppointments);
			appointmentsRow = row;
			slotTable.setWidget(row++, column, vpAppointments);
			column++;
		}
		
		slotTable.getRowFormatter().addStyleName(appointmentsRow, "slotAppointmentRow");
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
					for (User p:resources) {
						MultiWordSuggestion suggestion = new SearchSuggestion<IResource>(p, replacement.label(p), display.label(p));
						suggestions.add(suggestion);
					}
					Response response = new Response(suggestions);
					callback.onSuggestionsReady(request, response );
					
				}
			});
		}
	}; 

	private LabelFunc<User> lf = r -> r.getDisplayName();
			
//	private Widget createPersonSearchBox(int column, Slot slot) {
//		RemoteSearchBox<User> searchBox = new RemoteSearchBox<>(new SimpleTranslator(), runSearch, lf, lf);
//		searchBox.addSelectionHandler(r -> createAppointment(column, slot, searchBox));
//		searchBox.setWidth("18em");
//		return searchBox;
//	}

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
					Appointment a = new Appointment(host, guest, null, slot.getFrom(d), slot.getUntil(d), campaign.appointmentType);
					a.comment = txtComment.getText();
					eAgendaUI.service.saveAppointment(campaign, a, new RPCCallback<Appointment>() {
	
						@Override
						protected void onResult(Appointment result) {
//							Panel panel = appointmentPanels.get(column);
//							Widget renderer = createAppointmentRenderer(result, panel);
//							panel.add(renderer);
							
							Scheduler.get().scheduleDeferred(()-> {
								searchBox.setSelected(null);
								searchBox.setFocus(true);
							} );
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
		
		RemoteSearchBox<User> searchBox = new RemoteSearchBox<>(new SimpleTranslator(), runSearch, lf, lf);
		searchBox.addSelectionHandler(r -> createAppointment(column, slot, searchBox));
		searchBox.setWidth("18em");
		
		hp.add(l);
		hp.add(searchBox);
		
		
		return hp;
	}

//	private WorkPattern getPatternForDay(Date d) {
//		WorkPattern wp = null;
//		Iterator<WorkPattern> i = patterns.iterator();
//		
//		while (i.hasNext() && wp == null) {
//			WorkPattern curr = i.next();
//			
//			if (curr.applies(d)) {
//				wp = curr;
//			}
//		}
//			
//		return wp;
//	}
//
//	private Date getPreferredDate() {
//		Date preferredDate = dateBox.getValue();
//		
//		if (dateBox.getValue() == null) {
//			preferredDate = minDate;
//		} else {
//			if (minDate != null && dateBox.getValue().getTime() < minDate.getTime()) {
//				preferredDate = minDate;
//			} else if (maxDate != null && dateBox.getValue().getTime() > maxDate.getTime()) {
//				preferredDate = maxDate;
//			}
//		}
//		
//		return preferredDate == null ? new Date() : preferredDate;
//	}
//
//	public void updateMinMaxDates() {
//		
//		minDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
//		maxDate = new Date(0);
//		
//		patterns.forEach(p -> {
//			if (p.from == null || minDate == null || minDate.getTime() > p.from.getTime()) {
//				minDate = p.from;
//			}
//			
//			if (p.until == null || maxDate == null || maxDate.getTime() < p.until.getTime()) {
//				maxDate = p.until;
//			}
//		});
//	}
//	
	

}
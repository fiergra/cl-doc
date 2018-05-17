package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.command.ICommand;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchBox;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Day;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.Slot;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class WorkPatternEditor extends DockLayoutPanel {

	private FlexTable table = new FlexTable();
	ListBox cmbGrid = new ListBox();
	public static final String dayNames[] = {"MONDAY","TUESDAY","WEDNESDAY", "THURSDAY", "FRIDAY"};
	private WorkPattern pattern;
	
	private int START_H = 8;
	
	private String[] sGrids = new String[] {"10", "15", "30"};
	
	private int defaultCapacity = 5;
	final Campaign campaign;
	
	public WorkPatternEditor(Campaign campaign, WorkPattern pattern) {
		super(Unit.PX);
		this.campaign = campaign;
		this.pattern = pattern;
		
		addNorth(createHeader(), 42);
		add(new ScrollPanel(table));
		table.setStyleName("workPatternEditorTable");
		updateDisplay();
	}

	class SetWPDatesCommand extends CampaignCommand {

		private Date initialFrom;
		private Date initialUntil;
		
		private Date newFrom;
		private Date newUntil;
		private DateBox dpFrom;
		private DateBox dpUntil;
		
		public SetWPDatesCommand(Campaign campaign, WorkPattern pattern, DateBox dpFrom, DateBox dpUntil) {
			super(campaign, "set validity dates");
			this.initialFrom = pattern.from;
			this.initialUntil = pattern.until;
			this.newFrom = dpFrom.getValue();
			this.newUntil = dpUntil.getValue();
			this.dpFrom = dpFrom;
			this.dpUntil = dpUntil;
		}

		@Override
		public void exec() {
			pattern.from = newFrom;
			pattern.until = newUntil;
			saveAndUpdate();
		}

		private void saveAndUpdate() {
			saveCampaign();
			dpFrom.setValue(pattern.from);
			dpUntil.setValue(pattern.until);
			
			if (changeHandler != null) {
				changeHandler.run();
			}
		}
		
		@Override
		public void undo() {
			pattern.from = initialFrom;
			pattern.until = initialUntil;
			saveAndUpdate();
		}
		
	}
	
	class SetGridCommand extends CampaignCommand {

		private int newGrid;
		private int initialGrid;

		public SetGridCommand(Campaign campaign, WorkPattern pattern, int newGrid) {
			super(campaign, "set minute grid to " + newGrid);
			this.newGrid = newGrid;
			this.initialGrid = pattern.minuteGrid;
		}

		@Override
		public void exec() {
			pattern.minuteGrid = newGrid;
			saveCampaign();
			updateDisplay();
		}

		@Override
		public void undo() {
			pattern.minuteGrid = initialGrid;
			saveCampaign();
			updateDisplay();
		}
		
	}
	
	
	class SetLocationCommand extends CampaignCommand {

		private Room newLocation;
		private Room initialLocation;
		private SearchBox<Room> cmbLocation;

		public SetLocationCommand(Campaign campaign, WorkPattern pattern, Room newLocation, SearchBox<Room> cmbLocation) {
			super(campaign, "set location to " + (newLocation != null ? newLocation.getDisplayName() : "---"));
			this.newLocation = newLocation;
			this.initialLocation = pattern.location;
			this.cmbLocation = cmbLocation;
		}

		@Override
		public void exec() {
			pattern.location = newLocation;
			saveCampaign();
			cmbLocation.setSelected(pattern.location);
		}

		@Override
		public void undo() {
			pattern.location = initialLocation;
			saveCampaign();
			cmbLocation.setSelected(pattern.location);
		}
		
	}
	
	
	private RunSearch<Room> runSearch = new RunSearch<Room>() {

		@Override
		public void run(Request request, Callback callback, LabelFunc<Room> replacement, LabelFunc<Room> display) {
			eAgendaUI.service.findRooms(request.getQuery(), new RPCCallback<List<Room>>() {

				@Override
				protected void onResult(List<Room> resources) {
					Collection<Suggestion> suggestions = new ArrayList<SuggestOracle.Suggestion>();
					for (Room p:resources) {
						MultiWordSuggestion suggestion = new SearchSuggestion<IResource>(p, replacement.label(p), display.label(p));
						suggestions.add(suggestion);
					}
					Response response = new Response(suggestions);
					callback.onSuggestionsReady(request, response );
					
				}
			});
		}
	}; 
	


	
	private Widget createHeader() {
		HorizontalPanel hpHeader = new HorizontalPanel();
		hpHeader.setSpacing(3);
		hpHeader.setHeight("100%");
		hpHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		DateBox dpFrom = new DateBox();
		dpFrom.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd/MM/yyyy")));
		dpFrom.setWidth("7em");
		dpFrom.setValue(pattern.from);
		
		DateBox dpUntil = new DateBox();
		dpUntil.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd/MM/yyyy")));
		dpUntil.setWidth("7em");
		dpUntil.setValue(pattern.until);
		
		dpFrom.getDatePicker().addShowRangeHandler(e -> {
			if (dpUntil.getValue() != null && e.getEnd().getTime() > dpUntil.getValue().getTime()) {
				long curr = dpUntil.getValue().getTime() + 24 * 60 * 60 * 1000;;
				long end = e.getEnd().getTime();
				
				while (curr <= end) {
					dpFrom.getDatePicker().setTransientEnabledOnDates(false, new Date(curr));
					curr += 24 * 60 * 60 * 1000;
				}
			}
		});
		
		dpUntil.getDatePicker().addShowRangeHandler(event -> {
			if (dpFrom.getValue() != null && event.getStart().getTime() < dpFrom.getValue().getTime()) {
				long curr = event.getStart().getTime();
				long end = Math.min(dpFrom.getValue().getTime(), event.getEnd().getTime());
				
				while (curr < end) {
					dpUntil.getDatePicker().setTransientEnabledOnDates(false, new Date(curr));
					curr += 24 * 60 * 60 * 1000;
				}
			}
		});
		
		dpFrom.setFireNullValues(true);
		dpFrom.addValueChangeHandler(e->eAgendaUI.commando.execute(new SetWPDatesCommand(campaign, pattern, dpFrom, dpUntil)));
		dpUntil.setFireNullValues(true);
		dpUntil.addValueChangeHandler(e->eAgendaUI.commando.execute(new SetWPDatesCommand(campaign, pattern, dpFrom, dpUntil)));

		cmbGrid.setVisibleItemCount(1);
		for (String s:sGrids) {
			cmbGrid.addItem(s + " minutes", s);
		}

		cmbGrid.addChangeHandler(e -> {
			MessageBox.show("Sure?", "Sure...?", MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION, i -> { 
				if (i == MessageBox.MB_YES) {
					eAgendaUI.commando.execute(new SetGridCommand(campaign, pattern, Integer.valueOf(cmbGrid.getSelectedValue())));
				} else {
					cmbGrid.setSelectedIndex(Arrays.asList(sGrids).indexOf(String.valueOf(pattern.minuteGrid)));
				}
			});
		});

		IntegerBox defaultCapacityBox = new IntegerBox();
		defaultCapacityBox.setWidth("2em");
		defaultCapacityBox.setValue(defaultCapacity);
		defaultCapacityBox.addChangeHandler(e -> defaultCapacity = defaultCapacityBox.getValue());


		hpHeader.add(dpFrom);
		hpHeader.add(dpUntil);
		
		if (pattern.resource instanceof User) {
			RemoteSearchBox<Room> cmbLocations = new RemoteSearchBox<>(new SimpleTranslator(), runSearch, r-> r != null ? r.getDisplayName() : "---", r-> r != null ? r.getDisplayName() : "---");
			hpHeader.add(cmbLocations);
			cmbLocations.setSelected(pattern.location);
			cmbLocations.addSelectionHandler(e -> {
				eAgendaUI.commando.execute(new SetLocationCommand(campaign, pattern, cmbLocations.getSelected(), cmbLocations));
			});
		}
		hpHeader.add(cmbGrid);
		hpHeader.add(defaultCapacityBox);
		
		
		return hpHeader;
	}

//	private void setupTable() {
//		addHeader();
//		addTimeGrid();
//		displaySlots();
//	}
//

	private void addTimeGrid() {
		addTimeRows(0);
	}

	
	private FocusSlot fsStart = null;
	private Runnable changeHandler;
	
	private void onClick(FocusSlot fs) {
		if (fsStart == null) {
			fsStart = fs;
		} else {
			addSlots(fsStart, fs);
			displaySlots();
			fsStart = null;
		}
	}

	class AddRemoveSlotsCommand extends CampaignCommand {

		private List<Slot> slots;
		private boolean add;
		
		public AddRemoveSlotsCommand(Campaign campaign, String name, List<Slot> slots, boolean add) {
			super(campaign, name);
			this.slots = slots;
			this.add = add;
		}

		@Override
		public void exec() {
			if (add) {
				addSlots();
			} else {
				removeSlots();
			}
		}

		@Override
		public void undo() {
			if (add) {
				removeSlots();
			} else {
				addSlots();
			}
		}
		
		protected void addSlots() {
			slots.forEach(s -> s.day.addSlot(s));
			saveAndDisplay();
		}
		
		protected void removeSlots() {
			slots.forEach(s -> s.day.slots.remove(s));
			saveAndDisplay();
		}

		private void saveAndDisplay() {
			saveCampaign();
			addTimeGrid();
			displaySlots();
		}
		
	}
	
	
	private void addSlots(FocusSlot c0, FocusSlot c1) {
		int startD = Math.min(c0.dayIndex, c1.dayIndex);
		int endD = Math.max(c0.dayIndex, c1.dayIndex);

		int startM = Math.min(c0.hour * 60 + c0.minute, c1.hour * 60 + c1.minute);
		int endM = Math.max(c0.hour * 60 + c0.minute, c1.hour * 60 + c1.minute);
		
		List<Slot> slots = new ArrayList<>();
		
		for (int d = startD; d <= endD; d++) {
			Day day = pattern.getDay(d);
			
			for (int m = startM; m <= endM; m += pattern.minuteGrid) {
				Slot slot = new Slot(m / 60, m % 60, pattern.minuteGrid, defaultCapacity);
				slot.day = day;
				
				slots.add(slot);
//				day.addSlot(slot);
			}
		}

		ICommand c = new AddRemoveSlotsCommand(campaign, "add slots", slots, true);
		eAgendaUI.commando.execute(c);
		
	}
	
	
	private void displaySlots() {
		int column = 1;
		if (pattern.days != null) {
			for (Day day:pattern.days) {
				if (day.slots != null) {
					for (Slot slot:day.slots) {
						slot.day = day;
						int row = getRow(slot) + 1;
						
						int c = slot.m == 0 ? column + 1: column;
						table.setWidget(row, c, new SlotRenderer(this, slot));
						
					}
				}
				column++;
			}
		}
	}
	

	
	private int getRow(Slot slot) {
		int minutes = (slot.h - START_H) * 60 + slot.m;
		int row = minutes / pattern.minuteGrid;
		return row;
	}



	private static class FocusSlot extends Label {
		int dayIndex; 
		int hour;
		int minute;
		
		public FocusSlot(int dayIndex, int hour, int minute) {
			setStyleName("emptySlotRenderer");
			this.dayIndex = dayIndex;
			this.hour = hour;
			this.minute = minute;
			
			setTitle(hour + ":" + minute);
		}
	
	}
	
	private void addTimeRows(int column) {
		int row = 1;
		
		for (int h = START_H; h < 19; h++) {
			Label hLabel = new I18NLabel(String.valueOf(h));
			hLabel.setStyleName("hLabel");
			table.setWidget(row, column, hLabel);
			table.getFlexCellFormatter().setRowSpan(row, column, 60 / pattern.minuteGrid);
			
			for (int m = 0; m < 60; m += pattern.minuteGrid) {
				Label mLabel = new I18NLabel(String.valueOf(m));
				mLabel.setStyleName("mLabel");
				int c = column + (m == 0 ? 1 : 0);
				table.setWidget(row, c, mLabel);
				table.getRowFormatter().setStyleName(row, row % 2 == 0 ? "evenRow" : "oddRow");
				c++;
				for (int day = 0; day < 5; day++) {
					FocusSlot fs = new FocusSlot(day, h, m);
					fs.addClickHandler(e -> onClick(fs));
					table.setWidget(row, day + c, fs);
				}				
				
				row++;
			}
		}
	}
	
	
	private void addHeader() {
		table.getRowFormatter().setStyleName(0, "workPatternEditorHeader");
		for (int day = 0; day < 5; day++) {
			table.setWidget(0, day + 2, new I18NLabel(dayNames[day]));
			table.getFlexCellFormatter().setWidth(0, day + 2, "20%");
		}
	}

	public void updateDisplay() {
		table.removeAllRows();
		addHeader();
		addTimeGrid();
		displaySlots();
		cmbGrid.setSelectedIndex(Arrays.asList(sGrids).indexOf(String.valueOf(pattern.minuteGrid)));
	}

	public void setChangeHandler(Runnable changeHandler) {
		this.changeHandler = changeHandler;
	}

}

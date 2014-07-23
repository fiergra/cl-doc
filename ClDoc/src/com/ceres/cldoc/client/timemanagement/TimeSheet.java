package com.ceres.cldoc.client.timemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ceres.cldoc.Action;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.HumanBeingListBox;
import com.ceres.cldoc.client.views.LeaveRegistration;
import com.ceres.cldoc.client.views.MessageBox;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.ParticipationTimeFactory;
import com.ceres.cldoc.client.views.PersonEditor;
import com.ceres.cldoc.client.views.PopupManager;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.timemanagement.ActAsTimeSheetElement;
import com.ceres.cldoc.timemanagement.ITimeManagementService;
import com.ceres.cldoc.timemanagement.TimeSheetDay;
import com.ceres.cldoc.timemanagement.TimeSheetElement;
import com.ceres.cldoc.timemanagement.TimeSheetMonth;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.dynamicforms.client.DateLink;
import com.ceres.dynamicforms.client.DurationLink;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.TextLink;
import com.ceres.dynamicforms.client.WidgetCreator;
import com.ceres.dynamicforms.client.components.MapListRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeSheet extends DockLayoutPanel {

	private final ClDoc clDoc;
	private final HorizontalPanel timeSheetPanel = new HorizontalPanel();
	private final Label leaveBalanceLabel = new Label();
	private final Label hourBalanceLabel = new Label();
	private Person person;

	public TimeSheet(ClDoc clDoc) {
		this(clDoc, clDoc.getSession().getUser().getPerson());
	}
		
	public TimeSheet(ClDoc clDoc, Person person) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.person = person;
		loadAndDisplay(clDoc);
		addStyleName("timeSheet");
	}

	private void loadAndDisplay(ClDoc clDoc) {
		SRV.timeManagementService.loadTimeSheetYear(clDoc.getSession(), getPerson(), new Date().getYear() + 1900, new DefaultCallback<TimeSheetYear>(clDoc, "load timesheet") {

			@Override
			public void onResult(TimeSheetYear tsy) {
				setup(tsy);
			}
		});
	}
	
	private void reloadAndDisplay(ClDoc clDoc) {
		SRV.timeManagementService.loadTimeSheetYear(clDoc.getSession(), getPerson(), new Date().getYear() + 1900, new DefaultCallback<TimeSheetYear>(clDoc, "load timesheet") {

			@Override
			public void onResult(TimeSheetYear tsy) {
				timeSheetPanel.clear();
				createTimeSheet(timeSheetPanel, tsy);
			}
		});
	}
	
	protected void editWorkPattern(Person person, final TimeSheetMonth tsm) {
		final Interactor interactor =  new Interactor();
		Widget content = WidgetCreator.createWidget("<form><line label=\"Arbeitszeitmuster\" name=\"pattern\" type=\"Entity\" entityType=\"1001\" /> <!--<line name=\"von\" type=\"datebox\" required=\"true\"/><line name=\"bis\" type=\"datebox\"/>--></form>", interactor);
		DateTimeFormat dtf = DateTimeFormat.getFormat("LLLL yyyy");
		final Map<String, Serializable> item = new HashMap<String, Serializable>();
		item.put("pattern", tsm.getWp());
		
		PopupManager.showModal("Arbeitszeitmuster ab " + dtf.format(tsm.getDate()), content, new OnClick<PopupPanel>() {

			@Override
			public void onClick(final PopupPanel pp) {
				interactor.fromDialog(item);
				Entity pattern = (Entity)item.get("pattern");
				SRV.timeManagementService.setWorkPattern(clDoc.getSession(), getPerson(), pattern, tsm.getDate(), new DefaultCallback<Void>(clDoc, "save work pattern") {

					@Override
					public void onResult(Void result) {
						pp.hide();
						reloadAndDisplay(clDoc);
					}
				});
			}
		}, null);
		
		interactor.toDialog(item);
	}

	private Person getPerson() {
		return person;
	}
	
	private void setup(final TimeSheetYear tsy) {
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setStylePrimaryName("buttonsPanel");

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.add(buttons);

		final Label lbYear = new Label(String.valueOf(tsy.getYear()));
		header.add(lbYear);
		lbYear.addStyleName("timeSheetYear");
		
		Image lbRefresh = new Image("/icons/16/reload.png");
		lbRefresh.setStyleName("linkButton");
		buttons.add(lbRefresh);
		
		if (clDoc.getSession().isAllowed(new Action("TimeSheet", "EDIT"))) {
			final HumanBeingListBox hbl = new HumanBeingListBox(clDoc, null);
			buttons.add(hbl);
			hbl.addSelectionChangedHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (hbl.getSelected() != null) {
						setPerson(hbl.getSelected());
						reloadAndDisplay(clDoc);
					}
				}
			});
			
			Image pbNew = createWidget(SRV.c.newPPP(), "icons/32/Person-New-icon.png", new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					PersonEditor.editPerson(clDoc, new Person(), new OnClick<Person>(){

						@Override
						public void onClick(Person person) {
							editWorkRelationDetails(person);
							setPerson(person);
							reloadAndDisplay(clDoc);
						}

});

				}
			});
			pbNew.setPixelSize(18,18);
			buttons.add(pbNew);

		} else {
			Label nameLabel = new Label(getPerson().firstName + " " + getPerson().lastName);
			nameLabel.addStyleName("timeSheetYear");
			buttons.add(nameLabel);
		}
		
		addNorth(header, 3);

		buttons.add(lbYear);

		leaveBalanceLabel.addStyleName("balanceLabel");
		buttons.add(leaveBalanceLabel);
		buttons.add(hourBalanceLabel);
		
		Image lbExcel = new Image("/icons/16/Document-Microsoft-Excel.png");
		lbExcel.setStyleName("linkButton");
		buttons.add(lbExcel);
		lbExcel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				exportToExcel(tsy);
			}
		});

		
		lbRefresh.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reloadAndDisplay(clDoc);
			}
		});

		createTimeSheet(timeSheetPanel, tsy);
		add(new ScrollPanel(timeSheetPanel));
	}

	private Image createWidget(String label, String source,
			ClickHandler clickHandler) {
		Image img = new Image(source);
		img.setTitle(label);
		img.addStyleName("linkButton");
		img.addClickHandler(clickHandler);
		return img;
	}

	
	protected void exportToExcel(TimeSheetYear tsy) {
		String baseUrl = GWT.getModuleBaseURL();
		Window.open(baseUrl + "download?type=timesheet&id=" + getPerson().getId() , "_blank", "");
	}

	protected void setPerson(Person person) {
		this.person = person;
	}

	private void createTimeSheet(HorizontalPanel hp, TimeSheetYear tsy) {
		addStyleName("timeSheet");
		leaveBalanceLabel.setText("Resturlaub: " + tsy.getAbsenceBalance() + " (" + tsy.getAnnualLeaveDays() + "/" + tsy.getLeaveEntitlement() + ") ");
		int hb = tsy.getBalance();
		hourBalanceLabel.setText(getDurationAsString(hb));
		if (hb < 0) {
			hourBalanceLabel.removeStyleName("positiveBalance");
			hourBalanceLabel.addStyleName("negativeBalance");
		} else {
			hourBalanceLabel.removeStyleName("negativeBalance");
			hourBalanceLabel.addStyleName("positiveBalance");
		}
		List<TimeSheetElement>relevantMonths = new ArrayList<TimeSheetElement>();
		for (TimeSheetElement tsm:tsy.getChildren()) {
			VerticalPanel vp = new VerticalPanel();
			relevantMonths.add(tsm);
			vp.add(createMonthLabel(relevantMonths, tsm));
			vp.add(createMonthTable(tsm));
			hp.add(vp);
		}
		
	}
	
	
	private Widget createMonthLabel(List<TimeSheetElement> previousMonths, final TimeSheetElement tsm) {
		VerticalPanel vp = new VerticalPanel();
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMMM");
		
		HorizontalPanel hp = new HorizontalPanel();
		Label monthLabel = new Label(dtf.format(tsm.getDate()));
		monthLabel.addStyleName("monthLabel");
		hp.add(monthLabel);
		
		if (tsm.getDate().getTime() < new Date().getTime()) {
			final Label balanceLabel = new Label();
			balanceLabel.setText(String.valueOf(getDurationAsString(tsm.getBalance())));
			if (tsm.getBalance() < 0) {
				balanceLabel.addStyleName("negativeBalance");
			} else {
				balanceLabel.addStyleName("positiveBalance");
			}
			hp.add(balanceLabel);
			
			class R implements Runnable {

				@Override
				public void run() {
					balanceLabel.setText(String.valueOf(getDurationAsString(tsm.getBalance())));
					if (tsm.getBalance() < 0) {
						balanceLabel.removeStyleName("positiveBalance");
						balanceLabel.addStyleName("negativeBalance");
					} else {
						balanceLabel.removeStyleName("negativeBalance");
						balanceLabel.addStyleName("positiveBalance");
					}
				}};
			
			for (TimeSheetElement tse:previousMonths) {
				tse.subscribe(new R());
			}
			
		}		
		vp.add(hp);
		
		
		if (clDoc.getSession().isAllowed(new Action("TimeSheet", "EDIT"))) {
			final WorkPattern workPattern = ((TimeSheetMonth)tsm).getWp();
			Label wpLabel = new Label(workPattern != null ? workPattern.getName() + "(" + ((TimeSheetMonth) tsm).getLeaveEntitlement(30) + ")" : "---");
			wpLabel.addStyleName("wpLabel");
			vp.add(wpLabel);
			wpLabel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					editWorkPattern(getPerson(), (TimeSheetMonth) tsm);
				}
			});
		}
		
		return vp;
	}


	private boolean isOverlapping(Interactor outer) {
		boolean overlapping = false;
		
		if (outer.isValid()) {
			Iterator<Interactor> iter = actListRenderer.getInteractors().iterator();
			while (iter.hasNext() && !overlapping) {
				Interactor inner = iter.next();
				if (inner != outer) {
					overlapping = overlaps(inner, outer);
					boolean innerValid = inner.isEmpty() || (!overlapping && inner.isValid());
					inner.hilite(innerValid);
					boolean outerValid = outer.isEmpty() || (!overlapping && outer.isValid());
					outer.hilite(outerValid);
				}
			}
		}
		
		return overlapping;
	}

	protected boolean overlaps(Interactor next, Interactor interactor) {
		boolean overlap = false;
		
		if (interactor.isValid() && interactor.isValid() && !next.isEmpty() && !interactor.isEmpty()) {
			Act act1 = new Act();
			act1.setParticipant(null, Participation.ADMINISTRATOR);
			Act act2 = new Act();
			act2.setParticipant(null, Participation.ADMINISTRATOR);
			
			interactor.fromDialog(act2);
			next.fromDialog(act1);
			
			Participation p1 = act1.getParticipation(Participation.ADMINISTRATOR);
			Participation p2 = act2.getParticipation(Participation.ADMINISTRATOR);
			
			if (p1.start != null && p1.end != null && p2.start != null && p2.end != null) {
				overlap = p2.start.getTime() > p1.start.getTime() && p2.start.getTime() < p1.end.getTime();
				overlap |= p2.end.getTime() > p1.start.getTime() && p2.end.getTime() < p1.end.getTime();
				overlap |= p2.start.getTime() < p1.start.getTime() && p2.end.getTime() > p1.end.getTime();
			}
		}
		return overlap;
	}


	
	private boolean isOverlapping() {
		boolean overlapping = false;
		
		Iterator<Interactor> outerIter = actListRenderer.getInteractors().iterator();
		while (outerIter.hasNext() && !overlapping) {
			Interactor outer = outerIter.next();
			overlapping = isOverlapping(outer);
		}
		return overlapping;
	}


	private MapListRenderer actListRenderer = null;

	private void editDay(final FlexTable ft, final int row, final TimeSheetDay tsd) {
		VerticalPanel vp = new VerticalPanel();
		actListRenderer = new MapListRenderer(new String[]{"von", "bis", "Bemerkung"}, 
				new Runnable() {
					@Override
					public void run() {
						isOverlapping();
					}
				}) {
			
			@Override
			protected Map<String, Serializable> newAct() {
				Act newAct = new Act(new ActClass(ITimeManagementService.WORKINGTIME_ACT));
				Date date = tsd.getDate();
				newAct.date = date; 
				newAct.setParticipant(getPerson(), Participation.ADMINISTRATOR, date, date);

				return newAct;
			}
			
			
			@Override
			protected void createNewRow(final int row, final Interactor interactor) {
				int col = 0;
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes.put("role", Participation.ADMINISTRATOR.code);
				attributes.put("which", "start");
				attributes.put("required", "true");
				final DateLink fromLink = new ParticipationTimeFactory().createLink(interactor, "von", attributes );
				setWidget(row, col, fromLink.getWidget());
				interactor.addLink(fromLink);
				col++;
				attributes.put("which", "end");
				final DateLink toLink = new ParticipationTimeFactory().createLink(interactor, "bis", attributes );
				setWidget(row, col, toLink.getWidget());
				interactor.addLink(toLink);
				col++;

				TextBox textBox = new TextBox();
				setWidget(row, col, textBox);
				interactor.addLink(new TextLink(interactor, "Bemerkung", textBox, null));
				col++;

//				col++;
//				final Label lblDuration = new Label();
//				setWidget(row, col, lblDuration);
				
				interactor.addLink(new DurationLink(interactor, fromLink, toLink) {

					@Override
					protected void hilite(boolean isValid) {
//						lblDuration.setText("---");
						if (isValid) {
							getRowFormatter().removeStyleName(row, "invalidContent");
							if (!isEmpty()) {
//								lblDuration.setText(getDurationAsString(fromLink.getWidget().getDate(), toLink.getWidget().getDate()));
							}
						} else {
							getRowFormatter().addStyleName(row, "invalidContent");
						}
					}
					
				});
				
			}
			
			@Override
			protected boolean canRemove(final Map<String, Serializable> row) {
				return true;
			}

			@Override
			protected boolean isValid(Interactor interactor) {
				return true;//!isOverlapping(interactor);
			}
		};
				
		vp.add(actListRenderer);
		actListRenderer.setActs(asMaps(tsd.getChildren()));
		
		PopupManager.showModal("Anwesenheit", vp, new OnClick<PopupPanel>() {
			
			@Override
			public void onClick(PopupPanel pp) {
				pp.hide();
				List<Map<String, Serializable>> changed = actListRenderer.getChangedObjects();
				SRV.actService.save(clDoc.getSession(), asActs(changed), 
						new DefaultCallback<List<Act>>(clDoc, "save acts") {

					@Override
					public void onResult(List<Act> result) {
						tsd.setActs(asActs(actListRenderer.getActs()));
						addDayRow(ft, row, tsd);
					}
				});
			}
		}, null);

	}

	protected List<Act> asActs(List<Map<String, Serializable>> maps) {
		List<Act> list = new ArrayList<Act>(maps.size());
		for (Map<String, Serializable> map:maps) {
			list.add((Act) map);
		}
		return list;
	}


	private List<Map<String, Serializable>> asMaps(List<TimeSheetElement> children) {
		
		List<Map<String, Serializable>> maps = null;
		
		if (children != null) {
			maps = new ArrayList<Map<String,Serializable>>(children.size());
			for (TimeSheetElement ts:children) {
				ActAsTimeSheetElement aats = (ActAsTimeSheetElement) ts;
				maps.add(aats.getAct());
			}
		}
		
		return maps;
	}


	private Widget createMonthTable(final TimeSheetElement tsm) {
		final FlexTable ft = new FlexTable();
		ft.addStyleName("monthTable");
		ft.getColumnFormatter().addStyleName(0, "dateColumn");
		
		ft.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Cell cell = ft.getCellForEvent(event);
				if (cell != null) {
					int index = cell.getRowIndex();
					TimeSheetElement day = tsm.getChildren().get(index);
					
					if (day.isAbsent()) {
						removeLeave(day.getAbsence());
					} else {
						editDay(ft, index, (TimeSheetDay) day);
					}
				}
			}
		});
		
		int row = 0;
		for (TimeSheetElement tse:tsm.getChildren()) {
			TimeSheetDay tsd = (TimeSheetDay)tse;
			addDayRow(ft, row, tsd);
			row++;
		}
		
		return ft;
	}	
	
	private boolean isHoliday(Act act) {
		return act.actClass.name.equals(LeaveRegistration.ANNUAL_LEAVE_ACT);
	}

	DateTimeFormat dtfRowHeader = DateTimeFormat.getFormat("dd., E");

	private void addDayRow(FlexTable ft, int row, final TimeSheetDay tsd) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("timeSheetRow");
		Label dayLabel = new Label(dtfRowHeader.format(tsd.getDate()));
		dayLabel.addStyleName("headerDate");

		hp.add(dayLabel);

		ft.setWidget(row, 0, hp);
		
		if (tsd.isAbsent()) {
			if (tsd.isAnnualLeave()) {
				ft.getRowFormatter().addStyleName(row, tsd.getAbsenceDays() == 1f ? "leaveDay" : "halfLeaveDay");
			} else {
				ft.getRowFormatter().addStyleName(row, tsd.getAbsenceDays() == 1f ? "sickLeaveDay" : "halfSickLeaveDay");
			}

		} 
		if (tsd.isPublicHoliday()) {
			ft.getRowFormatter().addStyleName(row, "weekendCell");
			dayLabel.addStyleName("weekendDate");
		}
		
		hp.setTitle(getToolTip(tsd));
		
		if (/*tsd.getDate().getTime() < new Date().getTime() &&*/ !tsd.isAbsent() && (tsd.getQuota() != 0 || tsd.getBalance() != 0)) {
			Label lbBalance = new Label(String.valueOf(getDurationAsString(tsd.getBalance())));
			hp.add(lbBalance);
			lbBalance.setStyleName(tsd.getBalance() >=  0 ? "positiveBalance" : "negativeBalance"); 
		}			
		
		dayLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				registerLeave(getPerson(), tsd);
			}
		});
		
	}


	private String getToolTip(TimeSheetElement tse) {
		String tt = 
				getDurationAsString(tse.getBalance()) + "[" +  
				getDurationAsString(tse.getWorkingTime()) + "/" + 
				getDurationAsString(tse.getQuota()) + "]";

		return tt;
	}


	private String getDurationAsString(int duration) {
		NumberFormat nf = NumberFormat.getFormat("00");
		
		duration = duration < 0 ? -1 * duration : duration;
		int hours = duration / 60;
		int minutes = duration % 60;
		
		return nf.format(hours) + ":" + nf.format(minutes);
	}


	

	
	public void removeLeave(final Act act) {
		new MessageBox("Loeschen", "Wollen Sie " + (isHoliday(act) ? "den Urlaub" : "die Krankmeldung" ) + " stornieren?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

			@Override
			protected void onClick(int result) {
				if (result == MessageBox.MB_YES) {
					act.isDeleted = true;
					SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "delete") {

						@Override
						public void onResult(Act result) {
							reloadAndDisplay(clDoc);
						}
					});
				}
			}};
	}

	protected void registerLeave(Person person, final TimeSheetDay tsd) {
		final Interactor interactor =  new Interactor();
		Widget content = WidgetCreator.createWidget("<form>" +
				"<line label=\"abwesend von\" name=\"von\" type=\"datebox\" enabled=\"false\"/>" +
				"<line label=\"halbtags\" name=\""+ ITimeManagementService.HALFDAY_START + "\" type=\"boolean\"/>" +
				"<line label=\"bis\" name=\"bis\" type=\"datebox\" required=\"true\"/>" +
				"<line label=\"halbtags\" name=\""+ ITimeManagementService.HALFDAY_END + "\" type=\"boolean\"/>" +
				"<line label=\"\" name=\"leaveType\" type=\"option\" parent=\"MASTERDATA.LEAVETYPES\" required=\"true\"/>" +
				"</form>", interactor);
		DateTimeFormat dtf = DateTimeFormat.getFormat("LLLL");

		final Act leaveAct = new Act();
		leaveAct.put("von", tsd.getDate());
		leaveAct.put("bis", tsd.getDate());
		leaveAct.put("leaveType", new Catalog(191l));
		PopupManager.showModal("Abwesentheit registrieren", content, new OnClick<PopupPanel>() {

			@Override
			public void onClick(final PopupPanel pp) {
				interactor.fromDialog(leaveAct);
				Catalog leaveType = leaveAct.getCatalog("leaveType");
				leaveAct.actClass = new ActClass(leaveType.id == 191l ? ITimeManagementService.ANNUAL_LEAVE_ACT : ITimeManagementService.SICK_LEAVE_ACT);
				
				leaveAct.setParticipant(getPerson(), Participation.ADMINISTRATOR, tsd.getDate(), leaveAct.getDate("bis"));
				SRV.actService.save(clDoc.getSession(), leaveAct, new DefaultCallback<Act>(clDoc, "save leave") {

					@Override
					public void onResult(Act result) {
						pp.hide();
						reloadAndDisplay(clDoc);
					}
				});
				
			}
		}, null);
		
		interactor.toDialog(leaveAct);
	}

	private void editWorkRelationDetails(Person person) {
		User user = clDoc.getSession().getUser();
		SRV.entityService.listRelations(clDoc.getSession(), user.getPerson(), true, Catalog.WORKSFOR, new DefaultCallback<List<EntityRelation>>(clDoc, "") {

			@Override
			public void onResult(List<EntityRelation> result) {
				if (result.isEmpty()) {
					new MessageBox(clDoc.getLabel("missingER"), clDoc.getLabel("missingERText"), MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_EXCLAMATION).show();
				} else {
					final Interactor interactor =  new Interactor();
					Widget content = WidgetCreator.createWidget("<form><line label=\"Organisation\" name=\"orga\" type=\"Entity\" entityType=\"182\" /> " +
							"<line name=\"start\" type=\"datebox\" required=\"true\"/>" +
							"<line name=\"end\" label=\"bis\" type=\"datebox\"/>" +
							"<line name=\"AnnualLeaveRight\" label=\"Urlaubsanspruch\" type=\"float\"/>" +
							"</form>", interactor);
					final Map<String, Serializable> item = new HashMap<String, Serializable>();
					item.put("orga", result.get(0).object);
					item.put("start", new Date());
					
					PopupManager.showModal(clDoc.getLabel("Details"), content, new OnClick<PopupPanel>() {

						@Override
						public void onClick(final PopupPanel pp) {
							interactor.fromDialog(item);
							pp.hide();
						}
					}, null);
					
					interactor.toDialog(item);
				}
			}
		});
	}
	
}

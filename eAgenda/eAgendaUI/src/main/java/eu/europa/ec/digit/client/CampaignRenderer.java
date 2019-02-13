package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.command.ICommand;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchBox;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.ceres.dynamicforms.client.components.TabbedLayoutPanel;
import com.google.gwt.aria.client.OrientationValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.AppointmentType;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.EmailSettings;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class CampaignRenderer extends SplitLayoutPanel {

	private Label lbURL = new I18NLabel();
	private TextArea lbDescription = new TextArea();
	private CheckBox cbPublished = new CheckBox();

	private CheckBox cbDelegation = new CheckBox();
	private TextArea txtBody = new TextArea();
	private TextBox txtSubject = new TextBox();
	private CheckBox cbIncludeHost = new CheckBox();
	private ListBox lbStartDelay = new ListBox();

	private MultiSelectPanel<User> mspOwners;
//	private ListBox lbCityRestriction = new ListBox();
//	private ListBox lbOrgaRestriction = new ListBox();
	private DropDownSearchBox<IResource> sbResources;
	private final PushButton pbAdd = new PushButton(new Image("assets/images/24x24/add.png"));

	private TextBox txtType = new TextBox();
	private Integer[] durations = new Integer[] { 10, 15, 30, 45, 60, 90 };
	private ListBox lbDuration = new ListBox();

	private TabbedLayoutPanel tabCampaignDetails = new TabbedLayoutPanel(10, Unit.EM, OrientationValue.VERTICAL);
	private TabbedLayoutPanel tabMain = new TabbedLayoutPanel(42, Unit.PX);
	private TabbedLayoutPanel tabPatterns = new TabbedLayoutPanel(10, Unit.EM, OrientationValue.VERTICAL);
	private SlotAppointmentsView slotAppointmentsView;

	private Campaign campaign;

	class ChangeDescriptionCommand extends CampaignCommand {

		private TextBoxBase textBox;
		private String initialDesc;
		private String newDesc;

		public ChangeDescriptionCommand(Campaign campaign, String descr, TextBoxBase textBox) {
			super(campaign, "change description");
			this.textBox = textBox;
			this.initialDesc = campaign.description;
			this.newDesc = descr;
		}

		@Override
		public void exec() {
			campaign.description = newDesc;
			textBox.setText(newDesc);
			saveCampaign();
		}

		@Override
		public void undo() {
			campaign.description = initialDesc;
			textBox.setText(initialDesc);
			saveCampaign();
		}

	}

	private RunSearch<IResource> runSearch = new RunSearch<IResource>() {

		@Override
		public void run(Request request, Callback callback, LabelFunc<IResource> replacement, LabelFunc<IResource> display) {
			eAgendaUI.service.findResources(request.getQuery(), new RPCCallback<List<IResource>>() {

				@Override
				protected void onResult(List<IResource> resources) {
					Collection<Suggestion> suggestions = new ArrayList<SuggestOracle.Suggestion>();
					for (IResource p : resources) {
						MultiWordSuggestion suggestion = new SearchSuggestion<IResource>(p, replacement.label(p), display.label(p));
						suggestions.add(suggestion);
					}
					Response response = new Response(suggestions);
					callback.onSuggestionsReady(request, response);

				}
			});
		}
	};

	private Label l(String s) {
		Label l = new I18NLabel(s);
		l.setStyleName("formLabel");
		return l;
	}

	class SetAppointmentTypeCommand extends CampaignCommand {

		private AppointmentType originalType;
		private AppointmentType newType;

		public SetAppointmentTypeCommand(Campaign campaign, AppointmentType newType) {
			super(campaign, StringResources.getLabel("Set appointment type"));
			this.originalType = campaign.appointmentType;
			this.newType = newType;
		}

		@Override
		public void exec() {
			campaign.appointmentType = newType;
			saveCampaign();
			toDialog(campaign);
		}

		@Override
		public void undo() {
			campaign.appointmentType = originalType;
			saveCampaign();
			toDialog(campaign);
		}

	}

	public CampaignRenderer(Campaign c) {
		this.campaign = c;
		setStyleName("campaignRenderer");

		DockLayoutPanel dlpCampaign = new DockLayoutPanel(Unit.PX);

		sbResources = new DropDownSearchBox<>(runSearch, r -> getDisplayName(r));
		List<IResource> resources = campaign.assignedResources();
		sbResources.populate(resources);

		// tblCampaignDetails.setWidget(row, 0, sbResources);
		// tblCampaignDetails.getFlexCellFormatter().setColSpan(row, 0, 2);
		// tblCampaignDetails.getRowFormatter().setStyleName(row,
		// "resourceSelectionRow");
		// row++;
		sbResources.addSelectionHandler(e -> setSelectedResource(sbResources.getSelected()));
		sbResources.addDropDownChangeHandler(e -> setSelectedResource(sbResources.getSelected()));

		HorizontalPanel hpResourceSelector = new HorizontalPanel();
		hpResourceSelector.setStyleName("resourceSelectorPanel");
		hpResourceSelector.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpResourceSelector.setSize("100%", "100%");
		hpResourceSelector.add(sbResources);
		dlpCampaign.addSouth(hpResourceSelector, 42);

		tabCampaignDetails.add(createCampaignDetailsGeneral(), StringResources.getLabel("General"));
		tabCampaignDetails.add(createCampaignDetailsAccess(), StringResources.getLabel("Access"));
		tabCampaignDetails.add(createCampaignDetailsEmail(), StringResources.getLabel("Email"));
		dlpCampaign.add(tabCampaignDetails);

		slotAppointmentsView = new SlotAppointmentsView(campaign);
		tabMain.add(tabPatterns, "Patterns");

		pbAdd.setStyleName("blankButton");
		pbAdd.setPixelSize(24, 24);
		pbAdd.addClickHandler(e -> addWpForResource(sbResources.getSelected()));
		pbAdd.setVisible(false);
		
		
		tabPatterns.addWidget(pbAdd);

		tabMain.add(slotAppointmentsView, "Appointments");

		// addNorth(tblCampaignDetails, 250);
		addNorth(dlpCampaign, 250);
		add(tabMain);

		toDialog(c);
		if (!resources.isEmpty()) {
			IResource r = resources.get(0);
			sbResources.setSelected(r);
			setSelectedResource(r);
		}
	}

	private Widget createCampaignDetailsEmail() {
		FlexTable tblCampaignDetails = new FlexTable();
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");

		int row = 0;

		txtSubject.setWidth("100%");
		tblCampaignDetails.setWidget(row, 0, l("Subject"));
		tblCampaignDetails.setWidget(row, 1, txtSubject);
		row++;

		txtBody.setSize("100%", "4em");
		tblCampaignDetails.setWidget(row, 0, l("Confirmation"));
		tblCampaignDetails.setWidget(row, 1, txtBody);
		row++;

		tblCampaignDetails.setWidget(row, 0, l("Include host"));
		tblCampaignDetails.setWidget(row, 1, cbIncludeHost);
		row++;

		class EmailCmd extends AbstractCampaignCommand<EmailSettings> {

			public EmailCmd(Campaign campaign, String name, EmailSettings oldValue, EmailSettings newValue) {
				super(campaign, name, oldValue, newValue);
			}

			@Override
			public void exec() {
				campaign.emailSettings = newValue;
				saveCampaign();
				toDialog(campaign);
			}

			@Override
			public void undo() {
				campaign.emailSettings = this.oldValue;
				saveCampaign();
				toDialog(campaign);
			}

		}
		;

		ChangeHandler changeHandler = e -> {
			AbstractCampaignCommand<EmailSettings> command = new EmailCmd(campaign, StringResources.getLabel("change email settings"), new EmailSettings(campaign), new EmailSettings(txtSubject.getText(), txtBody.getText(), cbIncludeHost.getValue()));
			eAgendaUI.commando.execute(command);
		};

		ClickHandler clickHandler = e -> {
			AbstractCampaignCommand<EmailSettings> command = new EmailCmd(campaign, StringResources.getLabel("change email settings"), new EmailSettings(campaign), new EmailSettings(txtSubject.getText(), txtBody.getText(), cbIncludeHost.getValue()));
			eAgendaUI.commando.execute(command);
		};
		txtSubject.addChangeHandler(changeHandler);
		txtBody.addChangeHandler(changeHandler);
		cbIncludeHost.addClickHandler(clickHandler);

		return tblCampaignDetails;
	}

	private Widget createCampaignDetailsAccess() {
		FlexTable tblCampaignDetails = new FlexTable();
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");

		int row = 0;
		int column = 1;

		cbPublished.setTitle("published");
		cbPublished.addClickHandler(e -> publish(cbPublished.getValue()));

		HorizontalPanel hpRestrictions = new HorizontalPanel();
		hpRestrictions.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpRestrictions.setSpacing(3);

//		hpRestrictions.add(lbCityRestriction);
//		lbCityRestriction.addItem("---", "");
//		lbCityRestriction.addItem("BXL");
//		lbCityRestriction.addItem("LUX");
//		lbCityRestriction.addItem("IPR");
//
//		hpRestrictions.add(l("Organisation"));
//		lbOrgaRestriction.addItem("---");
//		lbOrgaRestriction.addItem("DIGIT.B");
//		lbOrgaRestriction.addItem("DIGIT.C");
//		hpRestrictions.add(lbOrgaRestriction);

		tblCampaignDetails.setWidget(row, 0, l("Owner"));
		mspOwners = new MultiSelectPanel<User>(campaign.owners, u -> u.userId) {

			@Override
			protected boolean onAdd() {
				addOwner(this);
				return false;
			}

			@Override
			protected boolean onDelete(User user) {
				eAgendaUI.commando.execute(new AddRemoveOwner(campaign, user, false, this));
				return true;
			}

		};
		tblCampaignDetails.setWidget(row++, column, mspOwners);

		tblCampaignDetails.setWidget(row, 0, l("Allow impersonation"));
		tblCampaignDetails.setWidget(row++, column, cbDelegation);
		
		cbDelegation.addClickHandler(e -> {
			ICommand command = new AbstractCampaignCommand<Boolean>(campaign, (campaign.allowDelegation ? "block" : "permit") + " delegation", campaign.allowDelegation, cbDelegation.getValue()) {

				@Override
				public void exec() {
					campaign.allowDelegation = newValue;
					saveCampaign();
					toDialog(campaign);
				}

				@Override
				public void undo() {
					campaign.allowDelegation = oldValue;
					saveCampaign();
					toDialog(campaign);
				}
				
			};
			eAgendaUI.commando.execute(command);
		});

		tblCampaignDetails.setWidget(row, 0, l("Restrictions"));
		tblCampaignDetails.setWidget(row++, column, hpRestrictions);

		return tblCampaignDetails;
	}

	private class SetStartDelayCommand extends AbstractCampaignCommand<Integer> {

		public SetStartDelayCommand(Campaign campaign, Integer oldValue, Integer newValue) {
			super(campaign, StringResources.getLabel("change start delay"), oldValue, newValue);
		}

		@Override
		public void exec() {
			campaign.startDelayInH = newValue;
			saveCampaign();
			toDialog(campaign);
		}

		@Override
		public void undo() {
			campaign.startDelayInH = oldValue;
			saveCampaign();
			toDialog(campaign);
		}
		
	}
	
	private Widget createCampaignDetailsGeneral() {
		FlexTable tblCampaignDetails = new FlexTable();
		// tblCampaignDetails.setSize("100%", "100%");
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");

		lbDescription.setStyleName("campaignDescription");
		lbDescription.addChangeHandler(e -> eAgendaUI.commando.execute(new ChangeDescriptionCommand(campaign, lbDescription.getText(), lbDescription)));

		int row = 0;
		int column = 1;

		tblCampaignDetails.setWidget(row, 0, l("Description"));
		tblCampaignDetails.setWidget(row, column, lbDescription);
		tblCampaignDetails.getCellFormatter().setHeight(row, 0, "100%");
		row++;

		cbPublished.setTitle("published");
		cbPublished.addClickHandler(e -> publish(cbPublished.getValue()));

		lbURL.setStyleName("hyperLink");
		lbURL.addClickHandler(e -> Window.open(lbURL.getText(), "_blank", ""));

		HorizontalPanel hpURL = new HorizontalPanel();
		hpURL.add(cbPublished);
		hpURL.add(lbURL);

		tblCampaignDetails.setWidget(row, 0, l("URL"));
		tblCampaignDetails.setWidget(row++, column, hpURL);

		HorizontalPanel hpAppointmentType = new HorizontalPanel();
		lbDuration.setVisibleItemCount(1);
		for (int i = 0; i < durations.length; i++) {
			lbDuration.addItem(String.valueOf(durations[i]));
		}

		hpAppointmentType.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpAppointmentType.setSpacing(3);
		hpAppointmentType.add(txtType);
		hpAppointmentType.add(new I18NLabel("Duration"));
		hpAppointmentType.add(lbDuration);
		hpAppointmentType.add(new I18NLabel("minutes"));

		ChangeHandler ch = e -> eAgendaUI.commando.execute(new SetAppointmentTypeCommand(campaign, new AppointmentType(txtType.getText(), getDuration(), "white")));
		txtType.addChangeHandler(ch);
		lbDuration.addChangeHandler(ch);

		tblCampaignDetails.setWidget(row, 0, l("Appointment type"));
		tblCampaignDetails.setWidget(row++, column, hpAppointmentType);

		lbStartDelay.addItem(StringResources.getLabel("in 1h"), "1");
		lbStartDelay.addItem(StringResources.getLabel("tomorrow"), "24");
		lbStartDelay.addItem(StringResources.getLabel("day after tomorrow"), "48");
		lbStartDelay.addItem(StringResources.getLabel("next week"), String.valueOf(24 * 7));

		lbStartDelay.addChangeHandler(e -> eAgendaUI.commando.execute(new SetStartDelayCommand(campaign, campaign.startDelayInH, Integer.valueOf(lbStartDelay.getSelectedValue()))));

		tblCampaignDetails.setWidget(row, 0, l("Start delay"));
		tblCampaignDetails.setWidget(row++, column, lbStartDelay);

		return tblCampaignDetails;
	}

	private int getDuration() {
		int index = lbDuration.getSelectedIndex();

		return index > -1 ? durations[index] : 15;
	}

	class AddRemoveOwner extends CampaignCommand {

		private User owner;
		private boolean add;
		private MultiSelectPanel<User> msp;

		public AddRemoveOwner(Campaign campaign, User owner, boolean add, MultiSelectPanel<User> multiSelectPanel) {
			super(campaign, (add ? "add " : "remove") + " owner '" + owner.userId + "' on campaign '" + campaign.name + "'");
			this.add = add;
			this.owner = owner;
			this.msp = multiSelectPanel;
		}

		private void addOwner() {
			if (campaign.addOwner(owner)) {
				saveCampaign();
				msp.refresh(campaign.owners);
			}
		}

		private void removeOwner() {
			if (campaign.removeOwner(owner)) {
				saveCampaign();
				msp.refresh(campaign.owners);
			}
		}

		@Override
		public void exec() {
			if (add) {
				addOwner();
			} else {
				removeOwner();
			}
		}

		@Override
		public void undo() {
			if (add) {
				removeOwner();
			} else {
				addOwner();
			}
		}

	}

	private void addOwner(MultiSelectPanel<User> multiSelectPanel) {
		PopupPanel popUp = new PopupPanel(true, false);
		SearchBox<IResource> sb = new RemoteSearchBox<>(new SimpleTranslator<IResource>(), runSearch, r -> r.getDisplayName(), r -> r.getDisplayName());
		sb.addSelectionHandler(s -> {
			IResource r = sb.getSelected();

			if (r instanceof User) {
				eAgendaUI.commando.execute(new AddRemoveOwner(campaign, (User) r, true, multiSelectPanel));
				popUp.hide();
			}
		});
		popUp.add(sb);
		popUp.showRelativeTo(multiSelectPanel);
		sb.setFocus(true);
	}

	class PublishCampaignCommand extends CampaignCommand {

		private boolean publish;

		public PublishCampaignCommand(Campaign campaign, boolean publish) {
			super(campaign, publish ? "publish campaign" : "un-publish campaign");
			this.publish = publish;
		}

		public void unPublish() {
			campaign.published = false;
			cbPublished.setValue(false);
			saveCampaign();
			updateURL();
		}

		public void publish() {
			campaign.published = true;
			cbPublished.setValue(true);
			saveCampaign();
			updateURL();
		}

		@Override
		public void undo() {
			if (publish) {
				unPublish();
			} else {
				publish();
			}
		}

		@Override
		public void exec() {
			if (publish) {
				publish();
			} else {
				unPublish();
			}
		}

	}

	private void publish(boolean value) {
		eAgendaUI.commando.execute(new PublishCampaignCommand(campaign, value));
	}

	private String getDisplayName(IResource r) {
		return r != null ? r.getDisplayName() : "---";
	}

	private void setSelectedResource(IResource resource) {
		pbAdd.setVisible(resource != null);
		doDisplayPatterns(resource);
		doDisplayAppointments(resource);
	}

	private void doDisplayAppointments(IResource resource) {
		slotAppointmentsView.setHost(resource);
	}

	private void doDisplayPatterns(IResource resource) {
		tabPatterns.clear();
		List<WorkPattern> patterns = campaign.resourcePatterns(resource);
		if (patterns.isEmpty()) {
			addWpForResource(resource);
		} else {
			patterns.forEach(p -> {
				WorkPatternEditor workPatternEditor = new WorkPatternEditor(campaign, p);
				tabPatterns.add(workPatternEditor, createTab(p, workPatternEditor));
			});
		}
	}

	private void addWpForResource(IResource resource) {
		WorkPattern wp = new WorkPattern();
		wp.resource = resource;
		WorkPatternEditor workPatternEditor = new WorkPatternEditor(campaign, wp);
		Widget hpTab = createTab(wp, workPatternEditor);
		eAgendaUI.commando.execute(new AddDeletePatternCommand(campaign, wp, workPatternEditor, hpTab, false));
	}

	class AddDeletePatternCommand extends CampaignCommand {

		private WorkPattern workPattern;
		private WorkPatternEditor workPatternEditor;
		private Widget hpTab;
		private boolean delete;

		public AddDeletePatternCommand(Campaign campaign, WorkPattern workPattern, WorkPatternEditor workPatternEditor, Widget hpTab, boolean delete) {
			super(campaign, (delete ? "delete " : "add ") + getWpName(workPattern));

			this.workPattern = workPattern;
			this.workPatternEditor = workPatternEditor;
			this.hpTab = hpTab;
			this.delete = delete;
		}

		private void addWp() {
			campaign.addWorkPattern(workPattern);
			tabPatterns.add(workPatternEditor, hpTab);
			tabPatterns.selectTab(workPatternEditor);
			saveCampaign();
		}

		private void removeWp() {
			tabPatterns.removeTab(workPatternEditor);
			campaign.removeWorkPattern(workPattern);
			saveCampaign();
		}

		@Override
		public void exec() {
			if (delete) {
				removeWp();
			} else {
				addWp();
			}
		}

		@Override
		public void undo() {
			if (delete) {
				addWp();
			} else {
				removeWp();
			}
		}

	}

	private Widget createTab(WorkPattern wp, WorkPatternEditor workPatternEditor) {
		HorizontalPanel hpTab = new HorizontalPanel();

		Label tbName = new I18NLabel(getWpName(wp));

		Image imgDelete = new Image("assets/images/16x16/minus.png");
		PushButton pbDelete = new PushButton(imgDelete);
		pbDelete.setStyleName("blankButton");
		pbDelete.setPixelSize(24, 24);
		pbDelete.addClickHandler(e -> eAgendaUI.commando.execute(new AddDeletePatternCommand(campaign, wp, workPatternEditor, hpTab, true)));

		// tbName.setWidth("100%");

		// HorizontalPanel hpButtonWrapper = new HorizontalPanel();
		// hpButtonWrapper.setStyleName("buttonWrapper");
		// hpButtonWrapper.add(pbDelete);
		// hpButtonWrapper.setWidth("100%");
		// hpButtonWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// hpTab.add(tbName);
		// hpTab.add(hpButtonWrapper);

		hpTab.add(tbName);
		hpTab.add(pbDelete);

		workPatternEditor.setChangeHandler(() -> tbName.setText(getWpName(wp)));

		return hpTab;

	}

	private String getWpName(WorkPattern wp) {
		String wpName;

		if (wp.getFrom() == null && wp.until == null) {
			wpName = "\u221E";
		} else if (wp.getFrom() == null) {
			wpName = "\u2192 " + ClientDateFormatter.format(wp.until);
		} else if (wp.until == null) {
			wpName = ClientDateFormatter.format(wp.getFrom()) + " \u2192 \u221E";
		} else {
			wpName = ClientDateFormatter.format(wp.getFrom(), wp.until);
		}

		return wpName;
	}

	private void toDialog(Campaign c) {
		lbDescription.setText(c.description);
		cbPublished.setValue(c.published);

		setValue(lbStartDelay, String.valueOf(c.startDelayInH));
		
		mspOwners.refresh(campaign.owners);
		cbDelegation.setValue(campaign.allowDelegation);
		
		if (c.appointmentType != null) {
			txtType.setText(c.appointmentType.name);
			int index = Arrays.asList(durations).indexOf(c.appointmentType.duration);
			lbDuration.setSelectedIndex(index);
		}

		if (campaign.emailSettings != null) {
			txtBody.setText(campaign.emailSettings.body);
			txtSubject.setText(campaign.emailSettings.subject);
			cbIncludeHost.setValue(campaign.emailSettings.includeHost);
		}
		updateURL();
	}

	private void setValue(ListBox lb, String value) {
		int indexOfValue = -1;
		int i = 0;
		
		while(i < lb.getItemCount() && indexOfValue == -1) {
			if (value.equals(lb.getValue(i))) {
				indexOfValue = i;
			} else {
				i++;
			}
		}
		
		lb.setSelectedIndex(indexOfValue);
	}

	void updateURL() {
		String baseUrl = GWT.getHostPageBaseURL();
		String url = baseUrl + "?" + (campaign.published ? "campaign=" + URL.encode(campaign.name) : "id=" + campaign.objectId);
		lbURL.setText(url);
	}

}

package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.command.ICommand;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchBox;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.AppointmentType;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.EmailSettings;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.User;

public class CampaignSettings extends DockLayoutPanel {

	public final static String ADMIN = "admin", OWNER ="owner", OPERATOR ="operator";
	private String[] roles = { ADMIN, OWNER, OPERATOR };

	private Label lbURL = new I18NLabel();
	private TextArea lbDescription = new TextArea();
	private CheckBox cbPublished = new CheckBox();

	private CheckBox cbDelegation = new CheckBox();
	private TextArea txtBody = new TextArea();
	private TextBox txtSubject = new TextBox();
	private CheckBox cbIncludeHost = new CheckBox();
	private ListBox lbStartDelay = new ListBox();

//	private MultiSelectPanel<User> mspOwners;
	private HashMap<String, MultiSelectPanel<User>> mspOwners = new HashMap<>();
//	private ListBox lbCityRestriction = new ListBox();
//	private ListBox lbOrgaRestriction = new ListBox();

	private TextBox txtType = new TextBox();
	private Integer[] durations = new Integer[] { 10, 15, 30, 45, 60, 90 };
	private ListBox lbDuration = new ListBox();

	public final Campaign campaign;

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

	public CampaignSettings(Campaign c) {
		super(Unit.PX);
		this.campaign = c;
		setStyleName("campaignRenderer");

		int row = 0;
		row = addHeading(row, "General");
		row = createCampaignDetailsGeneral(row);
		row = addHeading(row, "Access control");
		row = createCampaignDetailsAccess(row);
		row = addHeading(row, "E-mail");
		row = createCampaignDetailsEmail(row);

		add(tblCampaignDetails);

		toDialog(c);
	}

	private int addHeading(int row, String string) {
		Label settingsHeader = new I18NLabel(string);
		settingsHeader.setStyleName("campaignSettingsHeader");
		tblCampaignDetails.setWidget(row, 0, settingsHeader);
		tblCampaignDetails.getFlexCellFormatter().setColSpan(row, 0, 2);
		
		return ++row;
	}

	private FlexTable tblCampaignDetails = new FlexTable();

	private int createCampaignDetailsEmail(int row) {
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");


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

		return row;
	}

	private int createCampaignDetailsAccess(int row) {
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");

		int column = 1;

		cbPublished.setTitle("published");
		cbPublished.addClickHandler(e -> publish(cbPublished.getValue()));

//		HorizontalPanel hpRestrictions = new HorizontalPanel();
//		hpRestrictions.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		hpRestrictions.setSpacing(3);
//
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

		if (campaign.owners != null && !campaign.roles.containsKey(OWNER)) {
			campaign.roles.put(OPERATOR, campaign.owners);
		}
		
		boolean enabled = false;
		
		for (String role:roles) {
			tblCampaignDetails.setWidget(row, 0, l(role));
			
			Collection<User> users = campaign.roles.get(role);
			if (users == null) {
				users = new ArrayList<>();
				if (!enabled) {
					users.add(eAgendaUI.userContext.user);
				}
				campaign.roles.put(role, users);
			}
			MultiSelectPanel<User> mspRole = new MultiSelectPanel<User>(users, u -> u.userId, u -> u.person != null ? u.person.getDisplayName() : u.getDisplayName()) {

				@Override
				protected boolean onAdd() {
					addOwner(this, role);
					return false;
				}

				@Override
				protected boolean onDelete(User user) {
					eAgendaUI.commando.execute(new AddRemoveRole(campaign, role, user, false, this));
					return true;
				}

			};
			mspOwners.put(role, mspRole);
			enabled = enabled || users.contains(eAgendaUI.userContext.user); 
			mspRole.setEnabled(enabled);
			
			tblCampaignDetails.setWidget(row++, column, mspRole);
		}
		
		
//		tblCampaignDetails.setWidget(row, 0, l("Owner"));
//		mspOwners = new MultiSelectPanel<User>(campaign.owners, u -> u.userId) {
//
//			@Override
//			protected boolean onAdd() {
//				addOwner(this);
//				return false;
//			}
//
//			@Override
//			protected boolean onDelete(User user) {
//				eAgendaUI.commando.execute(new AddRemoveOwner(campaign, user, false, this));
//				return true;
//			}
//
//		};
//		tblCampaignDetails.setWidget(row++, column, mspOwners);

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

//		tblCampaignDetails.setWidget(row, 0, l("Restrictions"));
//		tblCampaignDetails.setWidget(row++, column, hpRestrictions);

		return row;
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
	
	private int createCampaignDetailsGeneral(int row) {
		tblCampaignDetails.setWidth("100%");
		tblCampaignDetails.getColumnFormatter().setWidth(0, "20%");
		tblCampaignDetails.getColumnFormatter().setWidth(1, "80%");

		lbDescription.setStyleName("campaignDescription");
		lbDescription.addChangeHandler(e -> eAgendaUI.commando.execute(new ChangeDescriptionCommand(campaign, lbDescription.getText(), lbDescription)));

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

		PushButton pbExportExcel = new PushButton(StringResources.getLabel("export to Excel..."));
		pbExportExcel.addClickHandler(e -> exportToExcel());
		tblCampaignDetails.setWidget(row, 0, new Image("assets/images/excel.png"));
		tblCampaignDetails.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		tblCampaignDetails.setWidget(row++, column, pbExportExcel);

		return row;
	}

	private void exportToExcel() {
		if (campaign != null) {
			String baseUrl = GWT.getModuleBaseURL();
			Window.open(baseUrl + "eagenda?export=" + campaign.objectId, "_blank", "");
		}
	}


	private int getDuration() {
		int index = lbDuration.getSelectedIndex();

		return index > -1 ? durations[index] : 15;
	}

	class AddRemoveRole extends CampaignCommand {

		private User owner;
		private boolean add;
		private MultiSelectPanel<User> msp;
		private String role;

		public AddRemoveRole(Campaign campaign, String role, User owner, boolean add, MultiSelectPanel<User> multiSelectPanel) {
			super(campaign, (add ? "add " : "remove") + role + " '" + owner.userId + "' on campaign '" + campaign.name + "'");
			this.add = add;
			this.role = role;
			this.owner = owner;
			this.msp = multiSelectPanel;
		}

		private void addOwner() {
			if (campaign.addRole(role, owner)) {
				saveCampaign();
				msp.refresh(campaign.roles.get(role));
			}
		}

		private void removeOwner() {
			if (campaign.removeRole(role, owner)) {
				saveCampaign();
				msp.refresh(campaign.roles.get(role));
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

//	class AddRemoveOwner extends CampaignCommand {
//
//		private User owner;
//		private boolean add;
//		private MultiSelectPanel<User> msp;
//
//		public AddRemoveOwner(Campaign campaign, User owner, boolean add, MultiSelectPanel<User> multiSelectPanel) {
//			super(campaign, (add ? "add " : "remove") + " owner '" + owner.userId + "' on campaign '" + campaign.name + "'");
//			this.add = add;
//			this.owner = owner;
//			this.msp = multiSelectPanel;
//		}
//
//		private void addOwner() {
//			if (campaign.addOwner(owner)) {
//				saveCampaign();
//				msp.refresh(campaign.owners);
//			}
//		}
//
//		private void removeOwner() {
//			if (campaign.removeOwner(owner)) {
//				saveCampaign();
//				msp.refresh(campaign.owners);
//			}
//		}
//
//		@Override
//		public void exec() {
//			if (add) {
//				addOwner();
//			} else {
//				removeOwner();
//			}
//		}
//
//		@Override
//		public void undo() {
//			if (add) {
//				removeOwner();
//			} else {
//				addOwner();
//			}
//		}
//
//	}

	private void addOwner(MultiSelectPanel<User> multiSelectPanel, String role) {
		PopupPanel popUp = new PopupPanel(true, false);
		SearchBox<IResource> sb = new RemoteSearchBox<>(new SimpleTranslator<>(), runSearch, r -> r.getDisplayName(), r -> r.getDisplayName());
		sb.addSelectionHandler(s -> {
			IResource r = sb.getSelected();

			if (r instanceof User) {
				eAgendaUI.commando.execute(new AddRemoveRole(campaign, role, (User) r, true, multiSelectPanel));
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

	private void toDialog(Campaign c) {
		lbDescription.setText(c.description);
		cbPublished.setValue(c.published);

		setValue(lbStartDelay, String.valueOf(c.startDelayInH));
		
		campaign.roles.entrySet().forEach(e -> {
			MultiSelectPanel<User> msp = mspOwners.get(e.getKey());
			if (msp != null) {
				msp.refresh(e.getValue());
			}

		});
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

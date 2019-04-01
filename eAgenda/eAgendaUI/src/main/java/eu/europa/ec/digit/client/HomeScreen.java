package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.command.CommandoButtons;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.AppointmentType;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Person;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.eAgenda.WorkPattern;
import eu.europa.ec.digit.shared.UserContext;

public class HomeScreen extends DockLayoutPanel {

//	private TabbedLayoutPanel tabPanel = new TabbedLayoutPanel(42, Unit.PX);
	private SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
	
	private EditableComboBox<CampaignSettings> cmbCampaigns = new EditableComboBox<>();


	public HomeScreen(UserContext userContext) {
		super(Unit.PX);

		ApplicationHeader header = createHeader(userContext);
		addNorth(header, 54);
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.addWest(createMenu(), 500);
		splitPanel.add(contentPanel);
		add(splitPanel);
		
		eAgendaUI.service.listCampaigns(new RPCCallback<List<Campaign>>() {

			@Override
			protected void onResult(List<Campaign> result) {
				if (result != null && !result.isEmpty()) {
					result.forEach(c -> {
						cmbCampaigns.addItem(new CampaignSettings(c));
					});
					cmbCampaigns.setSelectedItem(0, true);
				} else {
					createEmptyCampaign(userContext);
				}
 			}

		});
		
	}

	
	private void createEmptyCampaign(UserContext userContext) {
		Campaign campaign = new Campaign("<new campaign>", "<enter description here>", userContext.user, new AppointmentType("default",  15, "white"));
		eAgendaUI.service.saveCampaign(campaign, new RPCCallback<Campaign>() {

			@Override
			protected void onResult(Campaign result) {
				campaign.objectId = result.objectId;
				cmbCampaigns.addItem(new CampaignSettings(campaign));
				cmbCampaigns.setSelectedItem(0, true);
			}
		});
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

	private SingleSelectionMenu menu = new SingleSelectionMenu();
	private VerticalPanel vpTopMenuItems = new VerticalPanel();
	private VerticalPanel vpResourceMenuItems = new VerticalPanel();
	
	private Widget createMenu() {
		VerticalPanel vpMenu = new VerticalPanel();
		vpMenu.addStyleName("menuPanel");
		vpMenu.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		VerticalPanel vpMenuItems = new VerticalPanel();
		vpMenuItems.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		vpMenuItems.setSpacing(5);
		
		cmbCampaigns.getTextBox().addChangeHandler(e -> eAgendaUI.commando.execute(new ChangeNameCommand(cmbCampaigns.getSelectedItem(), cmbCampaigns.getTextBox().getText(), cmbCampaigns.getTextBox())));
		cmbCampaigns.setFormatter(c -> c.campaign.name);
		cmbCampaigns.setChangeHandler(selectedCampaign -> { 
			if (selectedCampaign != null) {
				setSelectedCampaign(selectedCampaign.campaign);
			} else {
				createEmptyCampaign(eAgendaUI.userContext);
			}
		});
		FlexTable hpMainItem = new FlexTable();
		hpMainItem.setStyleName("mainItem");
		hpMainItem.setWidth("100%");
		Image image = new Image("assets/images/64x64/calendar.white.png");
		hpMainItem.setWidget(0, 0, image);
		hpMainItem.setWidget(0, 1, cmbCampaigns);

		
		if (eAgendaUI.userContext.isAdmin()) {
			PushButton pbAdd = new PushButton(new Image("assets/images/24x24/add.white.png"));
			pbAdd.setStyleName("flatButton");
			pbAdd.addClickHandler(e -> { 
				Campaign newCampaign = new Campaign("<new campaign>", "<enter description here>", eAgendaUI.userContext.user, new AppointmentType("default",  15, "white"));
				if (eAgendaUI.commando != null) {
					eAgendaUI.commando.execute(new AddCampaignCommand(new CampaignSettings(newCampaign), cmbCampaigns));
				}
			});
	
			hpMainItem.setWidget(0, 2, pbAdd);
			pbAdd.setTitle(StringResources.getLabel("add new campaign"));
			
			PushButton pbRemove = new PushButton(new Image("assets/images/24x24/remove.white.png"));
			pbRemove.setStyleName("flatButton");
			hpMainItem.setWidget(0, 3, pbRemove);
			pbRemove.setTitle(StringResources.getLabel("remove current campaign"));
			pbRemove.addClickHandler(e -> eAgendaUI.commando.execute(new DeleteCampaignCommand(cmbCampaigns.getSelectedItem(), cmbCampaigns)));
		}
		
		cmbCampaigns.getTextBox().setStyleName("mainMenuItemTextBox");
		hpMainItem.getFlexCellFormatter().setWidth(0, 1, "100%");
		
		vpMenu.add(vpMenuItems);

		vpMenuItems.add(hpMainItem);
		vpMenuItems.add(vpTopMenuItems);
		vpTopMenuItems.setSpacing(5);

		if (eAgendaUI.userContext.isAdmin()) {
			menu.addItem(vpTopMenuItems, new Image("assets/images/24x24/menu.white.png"), "Settings", null, (m) -> displayWidget(cmbCampaigns.getSelectedItem()));
		}

		HorizontalPanel hpResources = new HorizontalPanel();
		hpResources.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		I18NLabel lbResourcesHeader = new I18NLabel("Resources");
		lbResourcesHeader.setStyleName("menuItemHeader");
//		PushButton pbNewResource = new PushButton(new Image("assets/images/24x24/add.white.png"));
//		pbNewResource.setStyleName("flatButton");
		hpResources.add(lbResourcesHeader);
//		hpResources.add(pbNewResource);
		
		if (eAgendaUI.userContext.isAdmin()) {
			RemoteSearchBox<IResource> sbResources = new RemoteSearchBox<>(new SimpleTranslator<IResource>(), runSearch, r -> r.getDisplayName(), r -> r.getDisplayName());
	//		pbNewResource.addClickHandler(e -> addNewResource(sbResources));
			sbResources.setStyleName("menuResourceSearchBox");
			sbResources.addStyleDependentName("empty");
			sbResources.setWidth("100%");
			sbResources.setText(StringResources.getLabel("<click here to search and add new>"));
			sbResources.addSelectionHandler(s -> {
				IResource r = sbResources.getSelected();
	
				if (r == null) {
					sbResources.setText(StringResources.getLabel("<click here to search and add new>"));
					sbResources.addStyleDependentName("empty");
				} else {
					sbResources.removeStyleDependentName("empty");
				}
				
				if (cmbCampaigns.getSelectedItem() != null) {
					Campaign campaign = cmbCampaigns.getSelectedItem().campaign; 
					List<WorkPattern> patterns = campaign.resourcePatterns(r);
					if (patterns.isEmpty()) {
						addResource(campaign, r);
						sbResources.setSelected(null);
					} else {
	// TODO select menu item
					}
				}
				
			});
			
			sbResources.getValueBox().addFocusHandler(e -> { 
				sbResources.removeStyleDependentName("empty");
				sbResources.setText(null); 
			});
			sbResources.getValueBox().addBlurHandler(e -> {
				sbResources.addStyleDependentName("empty");
				sbResources.setText(StringResources.getLabel("<click here to search and add new>"));
			});
	//		hpResources.setWidget(0, 0, lbResourcesHeader);
	//		hpResources.setWidget(1, 0, sbResources);
	//		hpResources.getFlexCellFormatter().setWidth(0, 1, "100%");
	//		vpMenuItems.add(hpResources);
			vpMenuItems.add(hpResources);
			vpMenuItems.add(sbResources);
		}
		vpResourceMenuItems.setSpacing(5);
		vpResourceMenuItems.setWidth("100%");
		vpMenuItems.add(vpResourceMenuItems);

		if (eAgendaUI.userContext.isAdmin()) {
			Widget adminWidget = new AdminStuff();
			menu.addItem(vpTopMenuItems, new Image("assets/images/24x24/gears.white.png"), "Administration", null, (m) -> displayWidget(adminWidget));
		}
		
		return vpMenu;
	}

	@SuppressWarnings("unused")
	private void addNewResource(RemoteSearchBox<IResource> sbResources) {
		Person resource = new Person();
		ResourceEditor<Person> re = new ResourceEditor<>(resource);
		MessageBox.show("New Resource", re, MessageBox.MB_OK | MessageBox.MB_CANCEL, MESSAGE_ICONS.MB_ICON_OK, MessageBox.NOP, 600, 400);
	}

	class AddRemoveResourceCommand extends CampaignCommand {

		private List<WorkPattern> workPattern;
		private boolean delete;
		private IResource resource;

		public AddRemoveResourceCommand(Campaign campaign, List<WorkPattern> patterns, boolean delete) {
			super(campaign, (delete ? "delete " : "add ") + patterns.get(0).resource.getDisplayName());
			this.resource = patterns.get(0).resource;
			this.workPattern = patterns;
			this.delete = delete;
		}

		private void addWp() {
			workPattern.forEach(workPattern -> campaign.addWorkPattern(workPattern));
			saveCampaign();
			if (cmbCampaigns.getSelectedItem().campaign.equals(campaign)) {
				MenuItem mItem = addResourceMenuItem(campaign, resource);
				menu.selectItem(mItem);
			}
		}

		private void removeWp() {
			workPattern.forEach(workPattern -> campaign.removeWorkPattern(workPattern));
			saveCampaign();
			menu.removeItem(vpResourceMenuItems, resource.getDisplayName());
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


	private void addResource(Campaign campaign, IResource resource) {
		WorkPattern wp = new WorkPattern();
		wp.resource = resource;
		wp.setFrom(new Date());
		List<WorkPattern> list = new ArrayList<>(1);
		list.add(wp);
		eAgendaUI.commando.execute(new AddRemoveResourceCommand(campaign, list, false));
	}

	private void removeResource(Campaign campaign, IResource resource) {
		List<WorkPattern> patterns = campaign.resourcePatterns(resource);
		eAgendaUI.commando.execute(new AddRemoveResourceCommand(campaign, patterns, true));
	}

	private void setSelectedCampaign(Campaign campaign) {
		populateResourcesMenu(campaign);
		menu.selectItem(0);
	}


	private void populateResourcesMenu(Campaign campaign) {
		vpResourceMenuItems.clear();
		if (campaign.assignedResources() != null) {
			campaign.assignedResources().forEach(r -> {
				addResourceMenuItem(campaign, r);
			});
		}
	}


	private MenuItem addResourceMenuItem(Campaign campaign, IResource r) {
		MenuItem mItem = menu.addItem(vpResourceMenuItems, getImage(r), r.getDisplayName(), new PatternsAndAppointments(campaign, r), (i) -> displayWidget(i.widget));

		if (eAgendaUI.userContext.isAdmin()) {
			PushButton pbDelete = new PushButton(StringResources.getLabel("delete"));
			pbDelete.setStyleName("menuItemDeleteButton");
			pbDelete.setVisible(false);
			pbDelete.addClickHandler(e -> {
				e.stopPropagation();
				MessageBox.show(StringResources.getLabel("Cancel Appointment"), StringResources.getLabel("Do you want to cancel this appointment?"), MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION, mr -> {
					if (mr == MessageBox.MB_YES) {
						removeResource(campaign, r);
					}
				});

			});
			mItem.hpRight.add(pbDelete);
			mItem.addMouseOverHandler(e -> pbDelete.setVisible(true));
			mItem.addMouseOutHandler(e -> pbDelete.setVisible(false));
		}		
		
		return mItem;
	}

	private void displayWidget(Widget widget) {
		contentPanel.clear();
		if (widget != null) {
			contentPanel.add(widget);
		}
	}

	class ChangeNameCommand extends CampaignCommand {

		private TextBox textBox;
		private String initialName;
		private String newName;
		private CampaignSettings campaignSettings;

		public ChangeNameCommand(CampaignSettings campaignSettings, String name, TextBox textBox) {
			super(campaignSettings.campaign, "set name to " + name);
			this.textBox = textBox;
			this.initialName = campaignSettings.campaign.name;
			this.newName = name;
			this.campaignSettings = campaignSettings;
		}

		@Override
		public void exec() {
			campaign.name = newName;
			saveCampaign();

			textBox.setText(newName);
			campaignSettings.updateURL();
		}

		@Override
		public void undo() {
			campaign.name = initialName;
			saveCampaign();

			textBox.setText(initialName);
			campaignSettings.updateURL();
		}
		
	}
	
	class DeleteCampaignCommand extends CampaignCommand {

		private EditableComboBox<CampaignSettings> cmbCampaigns;
		private CampaignSettings campaignSettings;

		public DeleteCampaignCommand(CampaignSettings campaignSettings, EditableComboBox<CampaignSettings> cmbCampaigns) {
			super(campaignSettings.campaign, "delete campaign " + campaignSettings.campaign.name);
			this.campaignSettings = campaignSettings;
			this.cmbCampaigns = cmbCampaigns;
		}

		@Override
		public void exec() {
			deleteCampaign();
			cmbCampaigns.removeItem(campaignSettings);
		}

		@Override
		public void undo() {
			saveCampaign();
			cmbCampaigns.addItem(campaignSettings);
			cmbCampaigns.setSelectedItem(campaignSettings, true);
		}
		
	}
	class AddCampaignCommand extends CampaignCommand {

		private EditableComboBox<CampaignSettings> tabPanel;
		private CampaignSettings campaignSettings;

		public AddCampaignCommand(CampaignSettings campaignSettings, EditableComboBox<CampaignSettings> tabPanel) {
			super(campaignSettings.campaign, "add new campaign");
			this.campaignSettings = campaignSettings;
			this.tabPanel = tabPanel;
		}

		@Override
		public void exec() {
			saveCampaign();
			tabPanel.addItem(campaignSettings);
			tabPanel.setSelectedItem(campaignSettings, true);
		}

		@Override
		public void undo() {
			deleteCampaign();
			tabPanel.removeItem(campaignSettings);
		}
		
	}
	private ApplicationHeader createHeader(UserContext userContext) {
		ApplicationHeader ah = new ApplicationHeader(userContext, "eAgenda");
//
//		
//		final PushButton pbAdd = new PushButton(new Image("assets/images/24x24/add.png"));
////		FAIcon pbAdd = new FAIcon("plus-square", 2);
//		pbAdd.setPixelSize(24, 24);
//		pbAdd.addClickHandler(e -> { 
//			Campaign newCampaign = new Campaign("<new campaign>", "<enter description here>", userContext.user, new AppointmentType("default",  15, "white"));
//			eAgendaUI.commando.execute(new AddCampaignCommand(newCampaign, tabPanel));
//		
//		});
//		ah.hpButtons.add(pbAdd);
//		tabPanel.addWidget(pbAdd);
		CommandoButtons cb = new CommandoButtons(eAgendaUI.commando);
		ah.hpLeft.add(cb);
//		ah.hpButtons.add(cb);
//		tabPanel.addWidget(cb);
		return ah;
	}

	private Image getImage(IResource resource) {
		return resource instanceof User ? new Image("assets/images/24x24/user.white.png") : new Image("assets/images/24x24/room.white.png");
	}


}

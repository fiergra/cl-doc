package eu.europa.ec.digit.client;

import java.util.List;

import com.ceres.dynamicforms.client.command.CommandoButtons;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import eu.europa.ec.digit.eAgenda.AppointmentType;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.shared.UserContext;

public class HomeScreen2 extends DockLayoutPanel {

	private TabbedLayoutPanel tabPanel = new TabbedLayoutPanel(42, Unit.PX);

	public HomeScreen2(UserContext userContext) {
		super(Unit.PX);

		ApplicationHeader header = createHeader(userContext);
		addNorth(header, 46);
		
//		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
//			
//			@Override
//			public boolean execute() {
//				setWidgetSize(header, 46);
//				return false;
//			}
//		}, 1000);
		
		add(tabPanel);
//		getWidgetContainerElement(header).addClassName("vExpandable");
//		addStyleName("tExpandable");
//		getElement().addClassName("tExpandable");
		
		eAgendaUI.service.listCampaigns(new RPCCallback<List<Campaign>>() {

			@Override
			protected void onResult(List<Campaign> result) {
				if (result != null) {
					result.forEach(c -> {
						CampaignRenderer campaignRenderer = new CampaignRenderer(c);
						tabPanel.add(campaignRenderer, createTab(c, campaignRenderer));
					});
				}
			}

		});
		
		
		
	}

	
	class ChangeNameCommand extends CampaignCommand {

		private TextBox textBox;
		private String initialName;
		private String newName;
		private CampaignRenderer campaignRenderer;

		public ChangeNameCommand(Campaign campaign, String name, TextBox textBox, CampaignRenderer campaignRenderer) {
			super(campaign, "set name to " + name);
			this.textBox = textBox;
			this.initialName = campaign.name;
			this.newName = name;
			this.campaignRenderer = campaignRenderer;
		}

		@Override
		public void exec() {
			campaign.name = newName;
			saveCampaign();

			textBox.setText(newName);
			campaignRenderer.updateURL();
		}

		@Override
		public void undo() {
			campaign.name = initialName;
			saveCampaign();

			textBox.setText(initialName);
			campaignRenderer.updateURL();
		}
		
	}
	
	class DeleteCampaignCommand extends CampaignCommand {

		private TabbedLayoutPanel tabPanel;
		private HorizontalPanel hpTab;
		private CampaignRenderer campaignRenderer;

		public DeleteCampaignCommand(Campaign campaign, TabbedLayoutPanel tabPanel, CampaignRenderer campaignRenderer, HorizontalPanel hpTab) {
			super(campaign, "delete campaign " + campaign.name);
			this.campaignRenderer = campaignRenderer;
			this.tabPanel = tabPanel;
			this.hpTab = hpTab;
		}

		@Override
		public void exec() {
			deleteCampaign();
			tabPanel.removeTab(campaignRenderer);
		}

		@Override
		public void undo() {
			saveCampaign();
			tabPanel.add(campaignRenderer, hpTab);
			tabPanel.selectTab(campaignRenderer);
		}
		
	}
	
	private Widget createTab(Campaign c, CampaignRenderer campaignRenderer) {
		HorizontalPanel hpTab = new HorizontalPanel();
		TextBox tbName = new TextBox();
		tbName.setStyleName("tabTextLabel");
		tbName.setText(c.name);
		tbName.addChangeHandler(e -> eAgendaUI.commando.execute(new ChangeNameCommand(c, tbName.getText(), tbName, campaignRenderer)));
		
		Image imgDelete = new Image("assets/images/16x16/minus.png");
		PushButton pbDelete = new PushButton(imgDelete);
		
//		FAIcon pbDelete = new FAIcon("minus-square", 1);
		pbDelete.setStyleName("blankButton");
		pbDelete.setPixelSize(24, 24);

		pbDelete.addClickHandler(e -> eAgendaUI.commando.execute(new DeleteCampaignCommand(c, tabPanel, campaignRenderer, hpTab)));
		
		hpTab.add(tbName);
		hpTab.add(pbDelete);
		
		return hpTab;
		
	}

	class AddCampaignCommand extends CampaignCommand {

		private CampaignRenderer campaignRenderer;
		private TabbedLayoutPanel tabPanel;
		private Widget w;

		public AddCampaignCommand(Campaign campaign, TabbedLayoutPanel tabPanel) {
			super(campaign, "add new campaign");
			this.tabPanel = tabPanel;
		}

		@Override
		public void exec() {
			saveCampaign();
			if (campaignRenderer == null) {
				this.campaignRenderer = new CampaignRenderer(campaign);
				this.w = createTab(campaign, campaignRenderer);
			}
			tabPanel.add(campaignRenderer, w);
			tabPanel.selectTab(campaignRenderer);
		}

		@Override
		public void undo() {
			deleteCampaign();
			tabPanel.remove(campaignRenderer);
		}
		
	}
	private ApplicationHeader createHeader(UserContext userContext) {
		ApplicationHeader ah = new ApplicationHeader(userContext, "eAgenda");

		
		final PushButton pbAdd = new PushButton(new Image("assets/images/24x24/add.png"));
//		FAIcon pbAdd = new FAIcon("plus-square", 2);
		pbAdd.setPixelSize(24, 24);
		pbAdd.addClickHandler(e -> { 
			Campaign newCampaign = new Campaign("<new campaign>", "<enter description here>", userContext.user, new AppointmentType("default",  15, "white"));
			eAgendaUI.commando.execute(new AddCampaignCommand(newCampaign, tabPanel));
		
		});
//		ah.hpButtons.add(pbAdd);
		tabPanel.addWidget(pbAdd);
		CommandoButtons cb = new CommandoButtons(eAgendaUI.commando);
//		ah.hpButtons.add(cb);
		tabPanel.addWidget(cb);
		return ah;
	}

}

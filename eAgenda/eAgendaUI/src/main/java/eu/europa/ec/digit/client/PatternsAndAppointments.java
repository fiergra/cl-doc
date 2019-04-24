package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.dynamicforms.client.components.TabbedLayoutPanel;
import com.google.gwt.aria.client.OrientationValue;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class PatternsAndAppointments extends DockLayoutPanel {

	private final PushButton pbAdd = new PushButton(new Image("assets/images/24x24/add.png"));

	private TabbedLayoutPanel tabMain = new TabbedLayoutPanel(42, Unit.PX);
	private TabbedLayoutPanel tabPatterns = new TabbedLayoutPanel(10, Unit.EM, OrientationValue.VERTICAL);
//	private SlotAppointmentsView slotAppointmentsView;

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

	public PatternsAndAppointments(Campaign c, IResource resource) {
		super(Unit.PX);
		this.campaign = c;
		setStyleName("campaignRenderer");

		add(tabMain);

		pbAdd.setStyleName("blankButton");
		pbAdd.setPixelSize(24, 24);
		pbAdd.addClickHandler(e -> addWpForResource(resource));
		pbAdd.setVisible(false);
		pbAdd.setTitle(StringResources.getLabel("add new working pattern for") +  " " + resource.getDisplayName());


		if (eAgendaUI.isOwner(campaign)) {
			HorizontalPanel hpPatternsLabel = new HorizontalPanel();
			Label lbPatterns = new I18NLabel("Patterns");
			lbPatterns.setStyleName("tabTextLabelSize");
			hpPatternsLabel.add(lbPatterns);
			hpPatternsLabel.add(pbAdd);
			tabMain.add(tabPatterns, hpPatternsLabel);
		}		

//		slotAppointmentsView = new SlotAppointmentsView(campaign);
//		tabMain.add(slotAppointmentsView, "Appointments");
		DailySlots dailySlots = new DailySlots(campaign, resource, new ArrayList<Date>(), false);
		dailySlots.setSelectedResource(campaign, resource);
		tabMain.add(new ScrollPanel(dailySlots), "Appointments");

		setSelectedResource(resource);
	}

	private void setSelectedResource(IResource resource) {
		pbAdd.setVisible(resource != null);
		doDisplayPatterns(resource);
//		doDisplayAppointments(resource);
	}

//	private void doDisplayAppointments(IResource resource) {
//		slotAppointmentsView.setHost(resource);
//	}

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
		pbDelete.setTitle(StringResources.getLabel("remove working pattern"));
		
		hpTab.add(tbName);
		HorizontalPanel hpRight = new HorizontalPanel();
		hpRight.setWidth("100%");
		hpRight.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hpRight.add(pbDelete);
		hpTab.add(hpRight);

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

}

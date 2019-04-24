package eu.europa.ec.digit.client;

import java.util.Collection;
import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.User;

public class CampaignFrontOffice extends DockLayoutPanel {

	private final ApplicationHeader ah;
	private DailySlots ftMain;

	public CampaignFrontOffice(Campaign campaign, Collection<Date> holidays) {
		super(Unit.PX);
		ah = new ApplicationHeader(eAgendaUI.userContext, campaign.name);
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
		ftMain = new DailySlots(campaign, null, holidays, true);
		add(new ScrollPanel(ftMain));
	}

	private String getClickHereText(User user) {
		return "Click <b>here</b> to fix appointments for </br>someone else than <b>" + getUserName(user) + "</b>";
	}

	private String getUserName(User user) {
		return user.person != null ? user.person.firstName + " " + user.person.lastName : user.userId;
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
			ftMain.setGuest(eAgendaUI.userContext.user);
		} else {
			ftMain.setGuest(psb.getSelected());
		}
		pup.hide();

		if (ftMain.getGuest().equals(eAgendaUI.userContext.user)) {
			ah.hiliteUser(false);
		} else {
			ah.hiliteUser(true);
		}
		
		clickHere.setHTML(getClickHereText(ftMain.getGuest()));
		ah.setUserName(ftMain.getGuest().person.firstName + " " + ftMain.getGuest().person.lastName);
	}

}

package eu.europa.ec.digit.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.shared.UserContext;

public class ApplicationHeader extends HorizontalPanel {

	public final HorizontalPanel hpLeft = new HorizontalPanel();
	public final HorizontalPanel hpRight = new HorizontalPanel();
	
	public final HTML imgUser = new FAIcon("user-circle", 2);
	public final HTML imgHelp = new FAIcon("question-circle", 2);

	public ApplicationHeader(UserContext userContext, String applicationTitle) {
		setStyleName("applicationHeader");

		setWidth("100%");
		setSpacing(2);
		
		hpLeft.setHeight("100%");
		hpLeft.setVerticalAlignment(ALIGN_MIDDLE);

		HorizontalPanel hpRightWrapper = new HorizontalPanel();
		hpRightWrapper.setSize("100%", "100%");
		hpRightWrapper.setVerticalAlignment(ALIGN_MIDDLE);
		hpRightWrapper.setHorizontalAlignment(ALIGN_RIGHT);

		hpRight.setHeight("100%");
		hpRight.setVerticalAlignment(ALIGN_MIDDLE);
		hpRight.setHorizontalAlignment(ALIGN_RIGHT);
		hpRight.setSpacing(5);
		
		hpRightWrapper.add(hpRight);

		add(hpLeft);
		add(hpRightWrapper);

		setup(userContext, applicationTitle);
	}

	private void setup(UserContext userContext, String applicationTitle) {
		Image institutionLogo = new Image("assets/images/logos/com." + StringResources.language + ".png");
		hpLeft.add(institutionLogo);

		Label lbApplicationTitle = new Label(applicationTitle);
		lbApplicationTitle.setStyleName("applicationTitle");
		hpLeft.add(lbApplicationTitle);

		imgUser.setTitle(userContext.user.userId);
		hpRight.add(imgUser);
		hpRight.add(imgHelp);
		
		imgHelp.addClickHandler(e -> setHeight());
	}

	private boolean large; 
	private void setHeight() {
		getElement().getParentElement().getStyle().setHeight(large ? 46 : 150, Unit.PX);
		large = !large;
	}

	public HandlerRegistration addUserClickHandler(ClickHandler ch) {
		return imgUser.addClickHandler(ch);
	}

	public void hiliteUser(boolean b) {
		if (b) {
			imgUser.addStyleName("hilite");
		} else {
			imgUser.removeStyleName("hilite");
		}
	}

	/*
	 * public class ApplicationHeader extends LayoutPanel {
	 * 
	 * public final HorizontalPanel hpButtons = new HorizontalPanel();
	 * 
	 * public ApplicationHeader(UserContext userContext, String applicationTitle) {
	 * setStyleName("applicationHeader"); setup(userContext, applicationTitle); }
	 * 
	 * private void setup(UserContext userContext, String applicationTitle) { Image
	 * institutionLogo = new Image("assets/images/logos/com." +
	 * StringResources.language + ".png"); add(institutionLogo);
	 * 
	 * institutionLogo.addLoadHandler(i -> { int ilWidth =
	 * institutionLogo.getOffsetWidth(); setWidgetLeftWidth(institutionLogo, 2,
	 * Unit.PX, ilWidth, Unit.PX); setWidgetTopBottom(institutionLogo, 2, Unit.PX,
	 * 2, Unit.PX); ilWidth += 4;
	 * 
	 * Label lbApplicationTitle = new Label(applicationTitle);
	 * lbApplicationTitle.setStyleName("applicationTitle"); add(lbApplicationTitle);
	 * setWidgetLeftWidth(lbApplicationTitle, ilWidth + 2, Unit.PX, 100, Unit.PCT);
	 * setWidgetTopBottom(lbApplicationTitle, 0, Unit.PX, 0, Unit.PX);
	 * 
	 * hpButtons.setHeight("100%"); hpButtons.setSpacing(3);
	 * hpButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	 * 
	 * add(hpButtons); setWidgetLeftWidth(hpButtons, ilWidth + 210, Unit.PX, 300,
	 * Unit.PX); setWidgetTopBottom(hpButtons, 0, Unit.PX, 0, Unit.PX);
	 * 
	 * HorizontalPanel hpUserControls = new HorizontalPanel();
	 * hpUserControls.setSize("100%", "100%"); hpUserControls.setSpacing(3);
	 * hpUserControls.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	 * hpUserControls.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	 * 
	 * Image imgUser = new Image("assets/images/User-icon.png");
	 * imgUser.setTitle(userContext.user.userId); PushButton pbUser = new
	 * PushButton(imgUser); pbUser.setStyleName("blankButton");
	 * pbUser.setEnabled(false); hpUserControls.add(pbUser); Image imgHelp = new
	 * Image("assets/images/Help-icon.png"); hpUserControls.add(imgHelp);
	 * 
	 * add(hpUserControls); setWidgetRightWidth(hpUserControls, 3, Unit.PX, 100,
	 * Unit.PX); setWidgetTopBottom(hpUserControls, 0, Unit.PX, 0, Unit.PX); }); }
	 */
}

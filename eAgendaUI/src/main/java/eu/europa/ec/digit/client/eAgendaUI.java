package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.command.Commando;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.shared.UserContext;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class eAgendaUI implements EntryPoint {
	public static final GWTeAgendaServiceAsync service = GWT.create(GWTeAgendaService.class);

	public static final Commando commando = new Commando();
	public static UserContext userContext;
	
	public void onModuleLoad() {
		StringResources.init(commando, ()->{
			RootPanel.get("bootstrapping").getElement().setInnerHTML("");
	
			HorizontalPanel hpWrapper = new HorizontalPanel();
			hpWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			hpWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			hpWrapper.setSize("100%", "100%");
			SimpleForm login = new SimpleForm();
			hpWrapper.add(login);

			TextBox txtUser = new TextBox();
			txtUser.setWidth("250px");
			PasswordTextBox txtPassword = new PasswordTextBox();
			txtPassword.setWidth("250px");
			
			login.addLine("user", txtUser);
			txtUser.setText("fiergra");
			login.addLine("password", txtPassword);
			
			PushButton pbLogin = new PushButton("Login...");
			pbLogin.setWidth("100px");
			login.addLine("", pbLogin);
	
	//		pbLogin.setEnabled(false);
			txtUser.addKeyPressHandler(e -> pbLogin.setEnabled(txtUser.getText() != null && txtUser.getText().length() > 0));
			pbLogin.addClickHandler(e -> service.login(txtUser.getText(), new RPCCallback<UserContext>() {
	
				@Override
				protected void onResult(UserContext userContext) {
					if (userContext != null) {
						RootLayoutPanel.get().remove(hpWrapper);
						eAgendaUI.userContext = userContext;
						setup();
					} else {
						txtUser.setText(null);
						txtUser.setFocus(true);
					}
				}
			}));
			
			centerWidget(hpWrapper);
			txtUser.setFocus(true);
		});
	}		

	private void centerWidget(Widget widget) {
//		FlexTable ft = new FlexTable();
//		ft.setSize("100%", "100%");
//		ft.setWidget(0, 0, widget);
		
		setMainWidget(widget);
//		
//		RootLayoutPanel r = RootLayoutPanel.get();
//		r.add(widget);
//		r.setWidgetHorizontalPosition(widget, Alignment.STRETCH);
//		r.setWidgetVerticalPosition(widget, Alignment.STRETCH);
	}
	
		
	public void setup() {
		if (userContext.isAdmin()) {
			
		}
		
		RootLayoutPanel.get().setStyleName("applicationBackground");
		
		String id = Window.Location.getParameter("id");
		String name = Window.Location.getParameter("campaign");
		String idOrName = id != null ? id : name;
		
		if (idOrName == null) {
			setMainWidget(new HomeScreen(userContext));
		} else {
			service.findCampaign(idOrName, new RPCCallback<Campaign>() {

				@Override
				protected void onResult(Campaign campaign) {
					if (campaign != null) {
						if (name != null && !campaign.published) {
							setMainWidget(new I18NLabel("specified campaign is not yet published"));
						} else {
							setMainWidget(new CampaignFrontOffice(campaign));
						}
					} else {
						setMainWidget(new I18NLabel("specified campaign cannot be loaded"));
					}
				}
			});
			
		}
	}

	private void setMainWidget(Widget widget) {
		RootLayoutPanel r = RootLayoutPanel.get();
		r.add(widget);
		r.setWidgetTopBottom(widget, 0, Unit.PX, 0, Unit.PX);
		r.setWidgetLeftRight(widget, 0, Unit.PX, 0, Unit.PX);
	}
	
	
	private static int busyCount = 0;
	
	public static void startBusy() {
		showBusyWidget(++busyCount > 0);
	}

	public static void stopBusy() {
		showBusyWidget(--busyCount > 0);
	}

	private static Image busyWidget = null;
	
	private static void showBusyWidget(boolean b) {
		LayoutPanel lp = RootLayoutPanel.get();
		if (b) {
			if (busyWidget == null) {
				busyWidget = new Image("assets/images/busy.gif");
				lp.add(busyWidget);
				lp.setWidgetBottomHeight(busyWidget, 10, Unit.PX, 80, Unit.PX);
				lp.setWidgetLeftWidth(busyWidget, 10, Unit.PX, 80, Unit.PX);
			}
		} else {
			lp.remove(busyWidget);
			busyWidget = null;
		}
	}

	public static void onFailure(Throwable caught) {
		stopBusy();
	}



	public static void onSuccess() {
		stopBusy();
	}



	public static String getLabel(String text) {
		return text;
	}
}

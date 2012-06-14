package com.ceres.cldoc.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.ceres.cldoc.IUserService;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.ClosableTab;
import com.ceres.cldoc.client.views.ConfiguredTabPanel;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.EntityFile;
import com.ceres.cldoc.client.views.Form;
import com.ceres.cldoc.client.views.LogOutput;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.OnOkHandler;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Organisation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClDoc implements EntryPoint {

	/**
	 * This is the entry point method.
	 */

	private ConfiguredTabPanel<ClDoc> mainTab;
	private final Label statusMessage = new Label();
	private Session session;
	private LogOutput logOutput;
	private final PickupDragController dragController = new PickupDragController(RootPanel.get(), false) {

		@Override
		protected void restoreSelectedWidgetsLocation() {
		}

		@Override
		protected void restoreSelectedWidgetsStyle() {
		}

		@Override
		protected void saveSelectedWidgetsLocationAndStyle() {
		}
		
	};
	
	public PickupDragController getDragController() {
		return dragController;
	}
	
	public Session getSession() {
		return session;
	}
	
	@Override
	public void onModuleLoad() {
		LoginScreen loginScreen = new LoginScreen(this, new OnOkHandler<Session>() {
			
			@Override
			public void onOk(Session result) {
				session = result;
				if (result != null) {
					if (session.getUser().hash == null) {
						setPassword(session);
					} else {
						setupMain(result);
					}
				} else {
					
				}
			}
		});
		RootLayoutPanel.get().add(loginScreen);
		
	}
	
	@SuppressWarnings("unchecked")
	protected void setPassword(final Session session) {
		final PasswordTextBox pwdField1 = new PasswordTextBox();
		final PasswordTextBox pwdField2 = new PasswordTextBox();
		Form createPwd = new Form(this, null, null){

			@Override
			protected void setup() {
				addLine("Passwort", pwdField1);
				addLine("Passwort Wiederholung", pwdField2);
			}
		};
		
		createPwd.showModal("Passwort definieren", new OnClick<Void>() {

			@Override
			public void onClick(Void pp) {
				SRV.userService.setPassword(session, session.getUser(), pwdField1.getText(), pwdField2.getText(), new DefaultCallback<Long>(ClDoc.this, "setPassword") {

					@Override
					public void onSuccess(Long result) {
						if (result.equals(IUserService.SUCCESS)) {
							setupMain(session);
						}
					}
				});
			}
		}, null, null);
	}

	private EntityFile getPersonalFile(Entity hb, Widget header, String config) {
		int count = mainTab.getWidgetCount();
		EntityFile personalFile = null;
		int index = 0;
		
		while (index < count && personalFile == null) {
			Widget tab = mainTab.getWidget(index);
			if (tab instanceof EntityFile && ((EntityFile)tab).getEntity().id.equals(hb.id) ) {
				personalFile = (EntityFile) tab;
			} else {
				index++;
			}
		}
		
		if (personalFile == null) {
			personalFile = new EntityFile(this, hb, header, config);
		}
		
		return personalFile;
	}
	
	public void openEntityFile(Entity entity, Widget header, String config) {
		EntityFile entityFile = getPersonalFile(entity, header, config);
		mainTab.add(entityFile, new ClosableTab(mainTab, entityFile, entity.id + " " + entity.name));
		mainTab.selectTab(mainTab.getWidgetIndex(entityFile));
	}
	
	public void status(String text) {
		statusMessage.setText(text);
		if (logOutput != null) {
			logOutput.log("status", text);
		}
	}

	public void clearStatus() {
		statusMessage.setText("");
	}
	
	private void setupMain(Session result) {
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		DockLayoutPanel hp = new DockLayoutPanel(Unit.PX);
		Image logo = getSessionLogo(session);
		hp.addWest(logo, 290);
		Label welcome = new Label(getDisplayName(result));
		hp.addEast(welcome, 200);
		
		mainPanel.addNorth(hp, 90);
		mainPanel.addSouth(statusMessage, 20);
		mainTab = new ConfiguredTabPanel<ClDoc>(ClDoc.this, "CLDOC.MAIN", ClDoc.this);
		mainPanel.add(mainTab);
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(mainPanel);
	}

	private Image getSessionLogo(Session session) {
		Organisation organisation = session.getUser().organisation;
		Image logo = new Image("icons/" + organisation.name + ".png");
		
		return logo;
	}

	private String getDisplayName(Session s) {
		return s.getUser().userName + "[" + s.getUser().person.firstName  + " " + s.getUser().person.lastName +"]";
	}

	public void setLogOutput(LogOutput logOutput) {
		this.logOutput = logOutput;
	}

	
	
}

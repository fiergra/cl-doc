package com.ceres.cldoc.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.Action;
import com.ceres.cldoc.IUserService;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.ActRenderer;
import com.ceres.cldoc.client.views.ClosableTab;
import com.ceres.cldoc.client.views.ConfiguredTabPanel;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.EntityFile;
import com.ceres.cldoc.client.views.LogOutput;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.OnOkHandler;
import com.ceres.cldoc.client.views.ParticipationTimeFactory;
import com.ceres.cldoc.client.views.PopupManager;
import com.ceres.cldoc.client.views.dynamicforms.CatalogMultiSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.CatalogSingleSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.EntitySelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.HumanBeingSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.PagesFactory;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.User;
import com.ceres.core.IApplication;
import com.ceres.core.IOrganisation;
import com.ceres.core.ISession;
import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.WidgetCreator;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClDoc implements EntryPoint, IApplication {

	/**
	 * This is the entry point method.
	 */

	private ConfiguredTabPanel<ClDoc> mainTab;
	
	private static long callId = 0;
	private ISession session;
	private LogOutput logOutput;
	private Image progress = new Image("images/progress.gif");
	
	@Override
	public ISession getSession() {
		return session;
	}
	
	@Override
	public void onModuleLoad() {
		WidgetCreator.addLinkFactory("pages", new PagesFactory());
		WidgetCreator.addLinkFactory("humanbeing", new HumanBeingSelectorFactory(this));
		WidgetCreator.addLinkFactory("entity", new EntitySelectorFactory(this));
		WidgetCreator.addLinkFactory("list", new CatalogSingleSelectorFactory(this, true));
		WidgetCreator.addLinkFactory("option", new CatalogSingleSelectorFactory(this, false));
		WidgetCreator.addLinkFactory("multiselect", new CatalogMultiSelectorFactory(this));
		WidgetCreator.addLinkFactory("participationtime", new ParticipationTimeFactory());
		
		
		String user = System.getProperty("user", null);
		String pwd = System.getProperty("pwd", null);
		if (user != null && pwd != null) {
			SRV.userService.login(user, pwd, new DefaultCallback<ISession>(this, "login") {

				@Override
				public void onResult(ISession result) {
					session = result;
					setupMain(result);
					preload(result);
				}
			});
		} else {
			
			LoginScreen loginScreen = new LoginScreen(this, new OnOkHandler<ISession>() {
				
				@Override
				public void onOk(ISession result) {
					session = result;
					if (result != null) {
						if (((User)session.getUser()).hash == null) {
							setPassword(session);
						} else {
							setupMain((Session) result);
							preload(result);
						}
					} else {
						
					}
				}
			});
			RootLayoutPanel.get().add(loginScreen);
		}
		
	}
	
	protected void preload(ISession result) {
		SRV.catalogService.listCatalogs(result, "ROLES", new AsyncCallback<List<Catalog>>() {
			
			@Override
			public void onSuccess(List<Catalog> result) {
				System.out.print(".");
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	protected void setPassword(final ISession session) {
		final PasswordTextBox pwdField1 = new PasswordTextBox();
		final PasswordTextBox pwdField2 = new PasswordTextBox();
		
		SimpleForm createPwd = new SimpleForm();
		createPwd.addLine("Passwort", pwdField1);
		createPwd.addLine("Passwort Wiederholung", pwdField2);
		PopupManager.showModal("Passwort definieren", createPwd, new OnClick<PopupPanel>() {

			@Override
			public void onClick(PopupPanel pp) {
				pp.hide();
				SRV.userService.setPassword(session, (User) session.getUser(), pwdField1.getText(), pwdField2.getText(), new DefaultCallback<Long>(ClDoc.this, "setPassword") {

					@Override
					public void onResult(Long result) {
						if (result.equals(IUserService.SUCCESS)) {
							setupMain((Session) session);
						}
					}
				});
			}
		}, null);
		
	}

	private EntityFile getPersonalFile(Entity hb, Widget header, String config) {
		int count = mainTab.getWidgetCount();
		EntityFile personalFile = null;
		int index = 0;
		
		while (index < count && personalFile == null) {
			Widget tab = mainTab.getWidget(index);
			if (tab instanceof EntityFile && ((EntityFile)tab).getEntity().getId().equals(hb.getId()) ) {
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
		Widget w = new ClosableTab(mainTab, entityFile, entity.getName());
//		w = new Label(entity.getName());
		w.setHeight("12px");
		mainTab.add(entityFile, w);
		mainTab.selectTab(mainTab.getWidgetIndex(entityFile));
	}
	
	@Override
	public void status(String text) {
//		statusMessage.setText(text);
		if (logOutput != null) {
			logOutput.log("status", text);
		}
	}

	public void clearStatus() {
//		statusMessage.setText("");
	}
	
	private void setupMain(final ISession session) {
		final LayoutPanel mainPanel = new LayoutPanel();

		final String open = Window.Location.getParameter("open");
		if (open != null) {
			openForm(mainPanel, open);
		} else {
			addSessionLogo(session, mainPanel);
			addProgressIndicator(mainPanel);
			
			SRV.catalogService.listCatalogs(session, "CLDOC.MAIN", new DefaultCallback <List<Catalog>>(this, "listCatalogs") {
				
				@Override
				public void onResult(List<Catalog> catalogs) {
					Widget mainWidget;
					
					filter(session, catalogs);
					if (catalogs.size() > 1) {
						mainWidget = mainTab = new ConfiguredTabPanel<ClDoc>(ClDoc.this, catalogs);
					} else if (catalogs.size() == 1){
						mainWidget = DynamicLoader.create(ClDoc.this, catalogs.get(0));
					} else {
						mainWidget = new Label("missing configuration in 'CLDOC.MAIN'");
					}
					mainPanel.add(mainWidget);
				}

				private void filter(ISession session, List<Catalog> catalogs) {
					Iterator<Catalog> iter = catalogs.iterator();
					while (iter.hasNext()) {
						if (!session.isAllowed(new Action(iter.next().code, Catalog.VIEW.code))) {
							iter.remove();
						}
					}
				}
				
			});

			
	
		}			
		RootLayoutPanel.get().clear();
		mainPanel.addStyleName("background");
		RootLayoutPanel.get().add(mainPanel);

	}

	private void addProgressIndicator(LayoutPanel mainPanel) {
		mainPanel.add(progress);
		progress.setStyleName("topOfTheRocks");
		progress.setVisible(false);
		mainPanel.setWidgetHorizontalPosition(progress, Alignment.END);
		mainPanel.setWidgetVerticalPosition(progress, Alignment.BEGIN);
	}

	private void addSessionLogo(ISession session, LayoutPanel mainPanel) {
		Image logo = getSessionLogo();
		Label welcome = new Label(getDisplayName(session));
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(5);
		vp.add(welcome);
		vp.add(logo);
		vp.setStyleName("topOfTheRocks");
		
		mainPanel.add(vp);
		mainPanel.setWidgetHorizontalPosition(vp, Alignment.END);
		mainPanel.setWidgetVerticalPosition(vp, Alignment.END);
	}

	private void openForm(LayoutPanel mainPanel, final String open) {
		final ActRenderer hp = new ActRenderer(this, new OnOkHandler<Integer>() {

			@Override
			public void onOk(Integer result) {
			}
		}, new Runnable() {

			@Override
 			public void run() {

			}
		});
		hp.addStyleName("viewer");
		mainPanel.add(hp);
		
		SRV.configurationService.getLayoutDefinition(getSession(), open, LayoutDefinition.FORM_LAYOUT, new DefaultCallback<LayoutDefinition>(this, "getLayoutDef") {

			@Override
			public void onResult(final LayoutDefinition ld) {
				if (ld != null) {
					SRV.actService.findByEntity(ClDoc.this.session, null, ClDoc.this.session.getUser().getOrganisation(), Participation.PROTAGONIST.id, 
							 null, null, new DefaultCallback<List<Act>>(ClDoc.this, "find by type") {

								@Override
								public void onResult(List<Act> result) {
									Act act = null;
									Iterator<Act> iter = result.iterator();
									
									while (iter.hasNext() && act == null) {
										Act next = iter.next();
										if (next.actClass.name.equals(open)) {
											act = next;
										}
									}
									if (act == null) {
										act = new Act(ld.actClass);
										act.setParticipant(session.getUser().getOrganisation(), Participation.PROTAGONIST, new Date(), null);
										act.setParticipant(session.getUser().getPerson(), Participation.ADMINISTRATOR, new Date(), null);
										act.setParticipant(session.getUser().getOrganisation(), Participation.ORGANISATION, new Date(), null);
									}
									hp.setAct(ld, act);
								}
					});
				}
			}
		});

		
	}

	public Image getSessionLogo() {
		IOrganisation organisation = session.getUser().getOrganisation();
		Image logo = new Image("icons/" + organisation.getName() + ".png");
		logo.setHeight("50px");
		return logo;
	}

	private String getDisplayName(ISession s) {
		return s.getUser().getUserName() + "[" + s.getUser().getPerson().getFirstName()  + " " + s.getUser().getPerson().getLastName() +"]";
	}

	public void setLogOutput(LogOutput logOutput) {
		this.logOutput = logOutput;
	}

	@Override
	public String getLabel(String label) {
		return label;
	}

	
	private HashMap<Long, String> asyncCalls = new HashMap<Long, String>();
	
	@Override
	public long startAsyncCall(String description) {
		callId++;
		asyncCalls.put(callId, description);
		progress.setVisible(true);
		return callId;
	}

	@Override
	public void stopAsyncCall(long callId) {
		asyncCalls.remove(callId);
		if (asyncCalls.isEmpty()) {
			progress.setVisible(false);
		}
	}

	
}

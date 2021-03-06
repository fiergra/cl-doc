package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.CatalogSelectionTree;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.User;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PoliciesPanel extends DockLayoutPanel {

	private final ClickableTable<User> userList;
	private final ClDoc clDoc; 
	
	public PoliciesPanel(final ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		userList = new ClickableTable<User>(clDoc) {

			@Override
			public boolean addRow(FlexTable table, int row, User user) {
				table.setWidget(row, 0, new Label(user.userName));
				table.setWidget(row, 1, new HTML(user.person.lastName + ",&nbsp;" + user.person.firstName));
				table.setWidget(row, 2, rolesList(user));
				return true;
			}

			private Widget rolesList(final User user) {
				HorizontalPanel hp = new HorizontalPanel();
				Image pbAdd = new Image("icons/16/File-New-icon.png");
				pbAdd.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						addRole(user);
					}
					
				});
				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				hp.add(pbAdd);
				for (final Catalog role : user.roles) {
					HorizontalPanel roleBox = new HorizontalPanel();
					
					if (role.parent == null) {
						Image pbRemove = new Image("icons/16/File-Delete-icon.png");
						pbRemove.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								removeRole(user, role);
							}
	
						});
						roleBox.add(pbRemove);
					}
					roleBox.add(new Label(role.code));
					roleBox.addStyleName("roleBox");
					roleBox.setSpacing(2);
					hp.add(roleBox);
				}
				return hp;
			}

			@Override
			protected void update(List<User> users) {
				super.update(users);
				getColumnFormatter().addStyleName(2, "hundertPercentWidth");
			}
			
			
		};
		final TextBox userTextBox = new TextBox();
		userList.addWidget(new Label("User"));
		userList.addWidget(userTextBox);
		userTextBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				userList.refresh();
			}
		});
		userList.setListRetrieval(new ListRetrievalService<User>() {
			
			@Override
			public void retrieve(String filter, AsyncCallback<List<User>> callback) {
				SRV.userService.listUsers(clDoc.getSession(), userTextBox.getText(), callback);
			}
		});
		
		add(userList);
	}
	
	private void removeRole(final User user, final Catalog role) {
		new MessageBox("Rolle entziehen", "", MessageBox.MB_YES | MessageBox.MB_NO, MESSAGE_ICONS.MB_ICON_QUESTION){

			@Override
			protected void onClick(int result) {
				if (result == MessageBox.MB_YES) {
					SRV.userService.removeRole(clDoc.getSession(), user, role, new DefaultCallback<Void>(clDoc, "") {

						@Override
						public void onResult(Void result) {
							userList.refresh();
						}
					});
				}
			}}.show();
	}
	
	private Catalog selected = null;
	
	private void addRole(final User user) {
		OnClick<Catalog> onSelect = new OnClick<Catalog>() {
			
			@Override
			public void onClick(Catalog role) {
				selected = role;
			}
		};

		OnClick<PopupPanel> onSave = new OnClick<PopupPanel>() {
			
			@Override
			public void onClick(final PopupPanel popup) {
				if (selected != null) {
					SRV.userService.addRole(clDoc.getSession(), user, selected, new DefaultCallback<Void>(clDoc, ""){

						@Override
						public void onResult(Void result) {
							popup.hide();
							userList.refresh();
						}});
				}
			}
		};
		Widget content = new CatalogSelectionTree(clDoc, onSelect, "ROLES");
		content.setPixelSize(400, 300);
		PopupManager.showModal("Rolle hinzufuegen", content, onSave , null);
		
	}
}

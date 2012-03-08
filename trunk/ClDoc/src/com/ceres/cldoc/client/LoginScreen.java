package com.ceres.cldoc.client;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.OnOkHandler;
import com.ceres.cldoc.client.views.PersonEditor;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginScreen extends HorizontalPanel {

	private static class OkEnabler implements KeyUpHandler {

		private TextBox tb1;
		private TextBox tb2;
		private Button button;

		public OkEnabler(TextBox tb1, TextBox tb2, Button button) {
			this.tb1 = tb1;
			this.tb2 = tb2;
			this.button = button;
			
			tb1.addKeyUpHandler(this);
			tb2.addKeyUpHandler(this);
		}
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			String text1 = tb1.getText();
			String text2 = tb2.getText();

			button.setEnabled(text1 != null  && text1.length() > 0);
//			button.setEnabled(text1 != null  && text2 != null && text1.length() > 0 && text2.length() > 0);
		}
		
	}

	private ClDoc clDoc;;
	
	public LoginScreen(final ClDoc clDoc, final OnOkHandler<Session> onOk) {
		setSize("100%", "100%");
		this.clDoc = clDoc;
		Grid g = new Grid(4, 3);
		final TextBox txtUserName = new TextBox();
		final PasswordTextBox txtPassWord = new PasswordTextBox();
		
		txtUserName.setWidth("10em");
		txtPassWord.setWidth("10em");

		Button pbOk = new Button(SRV.c.login());
		Button pbRegister = new Button(SRV.c.register());
		pbOk.setEnabled(false);

		pbOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SRV.userService.login(txtUserName.getText(), txtPassWord.getText(), 
						new DefaultCallback<Session>(clDoc, "") {

					@Override
					public void onSuccess(Session result) {
						if (result != null) {
							onOk.onOk(result);
						} else {
							txtPassWord.setText("");
							txtPassWord.setFocus(true);
						}
					}
				});
			}
		});
		new OkEnabler(txtUserName, txtPassWord, pbOk);
		
		g.setWidget(0, 0, new Image("clDOC_bw.png"));

		g.setWidget(1, 1, new Label(SRV.c.user()));
		g.setWidget(1, 2, txtUserName);

		g.setWidget(2, 1, new Label(SRV.c.password()));
		g.setWidget(2, 2, txtPassWord);
		
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttons.add(pbOk);
//		buttons.add(pbRegister);
		
		g.setWidget(3, 2, buttons);
		
		pbRegister.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final Person person = new Person();
				final TextBox txtNewUserName = new TextBox();
				final PasswordTextBox txtPwd1 = new PasswordTextBox();
				final PasswordTextBox txtPwd2 = new PasswordTextBox();

				final PersonEditor pe = new PersonEditor(clDoc, new PersonWrapper(person)) {
					
					@Override
					protected void setup() {
						addLine("Username", txtNewUserName);
						addLine("Password", txtPwd1);
						addLine("confirm password", txtPwd2);
						super.setup();
					}

					@Override
					protected void onModification() {
						super.onModification();
						String pwd1 = txtPwd1.getText();
						String pwd2 = txtPwd2.getText();
						
						
					}
					
					
					
				};
				
				OnClick<PersonWrapper> onClickSave = new OnClick<PersonWrapper>() {

					@Override
					public void onClick(PersonWrapper pp) {
						SRV.userService.register(person, txtNewUserName.getText(), txtPwd1.getText(), new DefaultCallback<Void>(clDoc, "register") {

							@Override
							public void onSuccess(Void result) {
								txtUserName.setText(txtNewUserName.getText());
								txtUserName.setFocus(true);
							}
						});
					}
				};
				
				pe.showModal("Register", onClickSave, null, new OnClick<PersonWrapper>() {

					@Override
					public void onClick(PersonWrapper pp) {
						pe.close();
					}
				});
			}
		});
		
		add(g);
		
		txtUserName.setFocus(true);
	}
}

package eu.europa.ec.digit.client;

import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Campaign;

public class AppointmentRenderer extends LayoutPanel {

	private Runnable onDelete;

	public AppointmentRenderer(Campaign campaign, Appointment a) {
		setStyleName("appointmentRenderer");
//		if (a.state != null) {
//			addStyleDependentName(a.state);
//		}
		
		setBackgroundColor(a);
		if (a.guest != null) {
			setTitle(a.guest.userId);
			if (a.guest.person == null) {
				Label userId = new I18NLabel(a.guest.userId);
				add(userId);
				setWidgetHorizontalPosition(userId, Alignment.BEGIN);
				setWidgetVerticalPosition(userId, Alignment.BEGIN);
				userId.setStyleName("mainLabel");
			} else {
				HorizontalPanel hpName = new HorizontalPanel();
				hpName.setSpacing(3);
				Label lastName = new I18NLabel(a.guest.person.lastName);
				lastName.setStyleName("mainLabel");
				Label firstName = new I18NLabel(a.guest.person.firstName);
				firstName.setStyleName("mainLabel2");
				
//				for (Entry<String, FiniteStateMachine> e:campaign.workflows.entrySet()) {
//					FiniteStateMachine fsm = e.getValue();
//					String state = a.getState(e.getKey(), fsm.initial);
//					hpName.add(new StateLabel(state, fsm.isInitial(state), fsm.isTerminal(state)));
//				}
				hpName.add(firstName);
				hpName.add(lastName);
	
				add(hpName);
				setWidgetHorizontalPosition(hpName, Alignment.BEGIN);
				setWidgetVerticalPosition(hpName, Alignment.END);
			}
		}	
		Label comment = new I18NLabel(a.comment);
		comment.setStyleName("commentLabel");
		add(comment);
		setWidgetHorizontalPosition(comment, Alignment.END);
		setWidgetVerticalPosition(comment, Alignment.BEGIN);
		
//		FAIcon pbDelete = new FAIcon("trash-alt", 2);

		HorizontalPanel allButtons = new HorizontalPanel();
//		campaign.workflows.entrySet().forEach(e -> {
//			WorkflowButtons buttons = new WorkflowButtons(a, e.getKey(), e.getValue());
//			allButtons.add(buttons);
//		});
		
		Image imgDelete = new Image("assets/images/delete.png");
		PushButton pbDelete = new PushButton(imgDelete);
		pbDelete.addClickHandler(e -> onDelete.run());
		pbDelete.setStyleName("blankButton");
		
		allButtons.add(pbDelete);
		add(allButtons);
		setWidgetHorizontalPosition(allButtons, Alignment.END);
		setWidgetVerticalPosition(allButtons, Alignment.END);
	}

	public void setOnDelete(Runnable onDelete) {
		this.onDelete = onDelete;
	}

	private void setBackgroundColor(Appointment appointment) {
		if (appointment.type != null && appointment.type.color != null) {
			getElement().getStyle().setBackgroundColor(appointment.type.color);
		}
	}


	
}

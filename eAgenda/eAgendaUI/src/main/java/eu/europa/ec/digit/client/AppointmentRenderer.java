package eu.europa.ec.digit.client;

import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.eAgenda.Appointment;

public class AppointmentRenderer extends LayoutPanel {

	private Runnable onDelete;

	public AppointmentRenderer(Appointment a) {
		setStyleName("appointmentRenderer");
		if (a.state != null) {
			addStyleDependentName(a.state);
		}
		
		setBackgroundColor(a);
		if (a.guest != null) {
			Label userId = new I18NLabel(a.guest.userId);
			add(userId);
			setWidgetHorizontalPosition(userId, Alignment.BEGIN);
			setWidgetVerticalPosition(userId, Alignment.BEGIN);
			
			if (a.guest.person == null) {
				userId.setStyleName("mainLabel");
			} else {
				HorizontalPanel hpName = new HorizontalPanel();
				hpName.setSpacing(3);
				Label lastName = new I18NLabel(a.guest.person.lastName);
				lastName.setStyleName("mainLabel");
				Label firstName = new I18NLabel(a.guest.person.firstName);
				firstName.setStyleName("mainLabel2");
				
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

		Image imgDelete = new Image("assets/images/delete.png");
		PushButton pbDelete = new PushButton(imgDelete);
		pbDelete.addClickHandler(e -> onDelete.run());
		pbDelete.setStyleName("blankButton");
//		imgDelete.setPixelSize(14, 14);
//		pbDelete.setPixelSize(16, 16);

		add(pbDelete);
		setWidgetHorizontalPosition(pbDelete, Alignment.END);
		setWidgetVerticalPosition(pbDelete, Alignment.END);
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

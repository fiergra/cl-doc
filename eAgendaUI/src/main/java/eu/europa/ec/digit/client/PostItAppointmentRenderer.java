package eu.europa.ec.digit.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import eu.europa.ec.digit.eAgenda.Appointment;


public class PostItAppointmentRenderer extends LayoutPanel {

	private HTML cancelText;
	public Appointment appointment;
	
	public PostItAppointmentRenderer(Appointment appointment) {
		this.appointment = appointment;
		addStyleName("postIt");
		
		Image imgPostIt = new Image("assets/images/postIt.png");
		imgPostIt.setPixelSize(275, 275);
		add(imgPostIt);
		setWidgetLeftWidth(imgPostIt, 0, Unit.PX, 275, Unit.PX);
		setWidgetTopHeight(imgPostIt, 0, Unit.PX, 275, Unit.PX);
		
		VerticalPanel vp = new VerticalPanel();
		add(vp);
		setWidgetLeftRight(vp, 40, Unit.PX, 40, Unit.PX);
		setWidgetTopBottom(vp, 40, Unit.PX, 40, Unit.PX);
		
		String sDate = ClientDateFormatter.dtfDayTime.format(appointment.from);
		String sLocation = appointment.location != null? appointment.location.getDisplayName() : "";
		String sDoctor = appointment.host != null? appointment.host.getDisplayName() : "";
		
		if (true) {//application.isAllowed(appointment, "CANCEL") || application.isAllowed(appointment, "DELETE")) {
			cancelText = new HTML(eAgendaUI.getLabel("howToCancel") + "<img src=\"assets/images/delete.png\"/>");
			vp.add(cancelText);
		}

		Label lDate = new Label(sDate); 
		Label lDoctor = new Label(sDoctor);
		Label lLocation = new Label(sLocation);

		lDate.setStyleName("postItText");
		lDoctor.setStyleName("postItText");
		lLocation.setStyleName("postItText");
		vp.add(new HTML("<br/>"));
		vp.add(lDate);
		vp.add(lDoctor);
		vp.add(lLocation);
		
	}

	public void setCancelHandler(ClickHandler onClickCancel) {
//		imgClose.addClickHandler(onClickCancel);
		if (cancelText != null) {
			cancelText.addClickHandler(onClickCancel);
		}
	}
}

package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import eu.europa.ec.digit.client.i18n.I18NLabel;
import eu.europa.ec.digit.client.i18n.StringResources;
import eu.europa.ec.digit.eAgenda.Appointment;


public class PostItAppointmentRenderer extends LayoutPanel {

	private I18NLabel cancelText;
	private Image cancelImage;
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
		String sLocation = appointment.getLocation() != null? appointment.getLocation().getDisplayName() : "";
		String sDoctor = appointment.host != null && appointment.host != appointment.getLocation() ? appointment.host.getDisplayName() : "";
		
		Label lDate = new Label(sDate); 
		Label lDoctor = new Label(sDoctor);
		Label lLocation = new Label(sLocation);

		lDate.setStyleName("postItText");
		lDoctor.setStyleName("postItText");
		lLocation.setStyleName("postItText");
		vp.add(lDate);
		vp.add(lDoctor);
		vp.add(lLocation);

		if (true) {//application.isAllowed(appointment, "CANCEL") || application.isAllowed(appointment, "DELETE")) {
			vp.add(new HTML("<br/>"));
			cancelText = new I18NLabel("howToCancel");
			cancelImage = new Image("assets/images/delete.png"); 
			vp.add(cancelImage);
//			cancelText = new HTML(StringResources.getLabel("howToCancel") + "<img src=\"assets/images/delete.png\"/>");
			cancelText.addStyleName("dynamicHyperLink");
			vp.add(cancelText);
		}

		
	}

	public void setCancelHandler(ClickHandler onClickCancel) {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MessageBox.show(StringResources.getLabel("Cancel Appointment"), StringResources.getLabel("Do you want to cancel this appointment?"), MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION, r -> {
					if (r == MessageBox.MB_YES) {
						onClickCancel.onClick(event);
					}
				});
			}
		};
		
		if (cancelText != null) {
			cancelText.addClickHandler(ch);
			cancelImage.addClickHandler(ch);
		}
	}
}

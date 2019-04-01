package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

public class AdminStuff extends DockLayoutPanel {

	public AdminStuff() {
		super(Unit.PX);

		SimpleForm form = new SimpleForm();
		PushButton pbMonitor = new PushButton("Monitor inbox");
		pbMonitor.addClickHandler(e -> {
			pbMonitor.setEnabled(false);
			eAgendaUI.service.monitorInbox(new RPCCallback<Boolean>() {

				@Override
				protected void onResult(Boolean result) {
					pbMonitor.setEnabled(true);
					if (result) {
						MessageBox.show("Success", "Inbox monitoring started!" , MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_ERROR, MessageBox.NOP);
					} else {
						MessageBox.show("Error", "Inbox monitoring could not be started." , MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_ERROR, MessageBox.NOP);
					}
				}
			});
		});
		form.addLine("", pbMonitor);
		add(form);

	}

	
}

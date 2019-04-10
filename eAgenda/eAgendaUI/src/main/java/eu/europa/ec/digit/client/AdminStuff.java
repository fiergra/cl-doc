package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.ceres.dynamicforms.client.SimpleForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.eAgenda.Campaign;

public class AdminStuff extends DockLayoutPanel {

	private Campaign campaign;
	
	public AdminStuff() {
		super(Unit.PX);

		SimpleForm form = new SimpleForm();
		PushButton pbMonitor = new PushButton(new Image("assets/images/outlook.png"));
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
		form.addLine("Monitor inbox", pbMonitor);
		
		PushButton pbExportExcel = new PushButton(new Image("assets/images/excel.png"));
		form.addLine("Export", pbExportExcel);
		pbExportExcel.addClickHandler(e -> exportToExcel());

		add(form);

	}

	private void exportToExcel() {
		if (campaign != null) {
			String baseUrl = GWT.getModuleBaseURL();
			Window.open(baseUrl + "eagenda?export=" + campaign.objectId, "_blank", "");
		}
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	
	
}

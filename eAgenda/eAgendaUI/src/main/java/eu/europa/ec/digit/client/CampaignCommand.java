package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.command.ICommand;

import eu.europa.ec.digit.eAgenda.Campaign;

public abstract class CampaignCommand implements ICommand {

	protected final Campaign campaign;
	private String name;

	public CampaignCommand(Campaign campaign, String name) {
		this.campaign = campaign;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return name;
	}
	
	protected void saveCampaign() {
		eAgendaUI.service.saveCampaign(campaign, new RPCCallback<Campaign>() {

			@Override
			protected void onResult(Campaign result) {
				campaign.objectId = result.objectId;
			}
		});
	}


	protected void deleteCampaign() {
		eAgendaUI.service.deleteCampaign(campaign, new RPCCallback<Campaign>() {

			@Override
			protected void onResult(Campaign result) {
				campaign.objectId = null;
			}
		});
	}



}

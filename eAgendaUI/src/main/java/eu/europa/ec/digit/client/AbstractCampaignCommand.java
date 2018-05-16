package eu.europa.ec.digit.client;

import eu.europa.ec.digit.eAgenda.Campaign;

public abstract class AbstractCampaignCommand<T> extends CampaignCommand {
	
	protected final T oldValue;
	protected final T newValue;

	public AbstractCampaignCommand(Campaign campaign, String name, T oldValue, T newValue) {
		super(campaign, name);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

//	@Override
//	public void exec() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void undo() {
//		// TODO Auto-generated method stub
//
//	}

}

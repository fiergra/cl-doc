package com.ceres.cldoc.client.service;

import com.google.gwt.core.client.GWT;

public abstract class SRV {

	public static final ConfigurationServiceAsync configurationService = GWT
	.create(ConfigurationService.class);

	public static final PersonServiceAsync personService = GWT
	.create(PersonService.class);

	public static final HumanBeingServiceAsync humanBeingService = GWT
	.create(HumanBeingService.class);

	public static final ValueBagServiceAsync valueBagService = GWT
	.create(ValueBagService.class);

}

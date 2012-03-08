package com.ceres.cldoc.client.service;

import com.ceres.cldoc.client.views.StringConstants;
import com.google.gwt.core.client.GWT;

public abstract class SRV {

	public static final StringConstants c = GWT
	.create(StringConstants.class);

	public static final ConfigurationServiceAsync configurationService = GWT
	.create(ConfigurationService.class);

	public static final ConfigurationServiceAsync catalogService = new CachedCatalogService();

	public static final UserServiceAsync userService = GWT
	.create(UserService.class);

	public static final HumanBeingServiceAsync humanBeingService = GWT
	.create(HumanBeingService.class);

	public static final ValueBagServiceAsync valueBagService = GWT
	.create(ValueBagService.class);


}

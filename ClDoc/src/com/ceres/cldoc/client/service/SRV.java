package com.ceres.cldoc.client.service;

import com.ceres.cldoc.client.timemanagement.TimeManagementService;
import com.ceres.cldoc.client.timemanagement.TimeManagementServiceAsync;
import com.ceres.cldoc.client.views.StringConstants;
import com.ceres.cldoc.model.Act;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SRV {

	public static final StringConstants c = GWT
	.create(StringConstants.class);

	public static final ConfigurationServiceAsync configurationService = GWT
	.create(ConfigurationService.class);

	public static final ConfigurationServiceAsync catalogService = GWT
			.create(ConfigurationService.class);

	public static final UserServiceAsync userService = GWT
	.create(UserService.class);

	public static final HumanBeingServiceAsync humanBeingService = GWT
	.create(HumanBeingService.class);

	public static final GWTEntityServiceAsync entityService = GWT
	.create(GWTEntityService.class);

	public static final ActServiceAsync actService = GWT
	.create(ActService.class);

	public static final TimeManagementServiceAsync timeManagementService = GWT
	.create(TimeManagementService.class);

	@SuppressWarnings("rawtypes")
	public static final AsyncCallback NOP = new AsyncCallback() {
		
		
		@Override
		public void onFailure(Throwable caught) {
		}

		@Override
		public void onSuccess(Object result) {
		}
	};
	
}

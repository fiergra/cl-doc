package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.core.IOrganisation;

public class OrganisationsPanel extends EntityFile<IOrganisation> {

	public OrganisationsPanel(ClDoc clDoc, IOrganisation organisation) {
		super(clDoc, organisation, clDoc.getSessionLogo(), "");
	}

}

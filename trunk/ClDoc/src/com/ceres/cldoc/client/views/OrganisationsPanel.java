package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Organisation;

public class OrganisationsPanel extends EntityFile<Organisation> {

	public OrganisationsPanel(ClDoc clDoc, Organisation organisation) {
		super(clDoc, organisation, clDoc.getSessionLogo(), "");
	}

}

package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.timemanagement.ITimeManagementService;
import com.ceres.cldoc.timemanagement.WorkPattern;

public class TimeManagementServiceTest extends TransactionalTest {

	public void testWorkpattern() throws Exception {
		ITimeManagementService as = Locator.getTimeManagementService();
		IEntityService entityService = Locator.getEntityService();

		List<Entity> wps = entityService.list(getSession(), 1001);
		int wpi = 0;
		Catalog c = Locator.getCatalogService().load(getSession(), "MASTERDATA.ER.arbeitet entsprechend");

		List<EntityRelation> relations = entityService.listRelations(getSession(), wps.get(wpi++), false, c);
		while (relations.isEmpty() && wpi < wps.size()) {
			relations = entityService.listRelations(getSession(), wps.get(wpi++), false, c);
		}
		assertFalse(relations.isEmpty());
		WorkPattern wp = as.getWorkPattern(getSession(), relations.get(0).subject);
		assertNotNull(wp);
	}
	
}

package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Participation;

public class LuceneServiceTest extends TransactionalTest {

	public void testAddAndRetrieve() throws Exception {
		ILuceneService ls = Locator.getLuceneService();
		ls.deleteIndex();
		
		Entity entity = new Entity();
		entity.setName("TestEntity");
		entity.type = Entity.ENTITY_TYPE_ORGANISATION; 
				
		Act masterData = new Act(new ActClass("TestEntity"));
		masterData.set("feld1", "value1");
		masterData.set("feld2", "wert2");

		Locator.getEntityService().save(getSession(), entity);
		masterData.setParticipant(entity, Participation.MASTERDATA, null, null);
		Locator.getActService().save(getSession(), masterData);
		
		ls.addToIndex(entity, masterData);
		ls.addToIndex(entity, masterData);
		List<Entity> entities = ls.retrieve("value*");
		
		assertNotNull(entities);
		assertEquals(1, entities.size());
	}
	
}

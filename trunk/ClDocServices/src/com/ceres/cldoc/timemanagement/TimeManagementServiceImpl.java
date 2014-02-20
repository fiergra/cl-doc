package com.ceres.cldoc.timemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.core.ISession;

public class TimeManagementServiceImpl implements ITimeManagementService {

	@Override
	public WorkPattern getWorkPattern(ISession session, Entity person) {
		Catalog c = Locator.getCatalogService().load(session, "MASTERDATA.ER.arbeitet entsprechend");
		List<EntityRelation> relations = Locator.getEntityService().listRelations(session, person, true, c);
		WorkPattern result = null;
		
		if (!relations.isEmpty()) {
			 result = new WorkPattern(relations.get(0).object);
			 result.weeklyHours = loadDetails(session, result);
		}
		
		return result;
	}

	private Float loadDetails(ISession session, final WorkPattern wp) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Float execute(Connection con) throws Exception {
				PreparedStatement s = con.prepareStatement("select * from WorkPattern where current_date > startdate and (current_date < enddate OR enddate is null) AND id = ?");
				s.setLong(1, wp.getId());
				ResultSet rs = s.executeQuery();
				Float result = rs.next() ? rs.getFloat("weeklyHours") : null;
				s.close();
				return result;
			}
		});
	}

}

package com.ceres.cldoc;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.Session;

public interface IReportService {
	List <ReportDefinition> list(Session session, Long type);
	List<HashMap<String, Serializable>> execute(Session session, ReportDefinition rd, IAct filters);
	ReportDefinition load(Session session, Catalog catalog);
	byte[] exportXLS(Session session, long reportId, IAct filters) throws IOException;
	
}

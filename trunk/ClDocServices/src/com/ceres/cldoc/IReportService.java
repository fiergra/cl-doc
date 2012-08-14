package com.ceres.cldoc;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.ReportDefinition;

public interface IReportService {
	List <ReportDefinition> list(Session session, Long type);
	List<HashMap<String, Serializable>> execute(Session session, ReportDefinition rd);
	byte[] exportXLS(Session session, long reportId) throws IOException;
	ReportDefinition load(Session session, Catalog catalog);
	
}

package com.ceres.cldoc;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.model.ISession;

public interface IReportService {
	List <ReportDefinition> list(ISession session, Long type);
	List<HashMap<String, Serializable>> execute(ISession session, ReportDefinition rd, IAct filters);
	ReportDefinition load(ISession session, Catalog catalog);
	byte[] exportXLS(ISession session, long reportId, IAct filters) throws IOException;
	
}

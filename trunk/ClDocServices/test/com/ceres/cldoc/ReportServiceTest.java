package com.ceres.cldoc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.model.ReportDefinition;

public class ReportServiceTest extends TransactionalTest {

	public void testList() {
		IReportService reportService = Locator.getReportService();
		List<ReportDefinition> reports = reportService.list(getSession(), null);
		assertNotNull(reports);
		assertFalse(reports.isEmpty());
	}

	public void testExec() {
		IReportService reportService = Locator.getReportService();
		List<HashMap<String, Serializable>> result = reportService.execute(getSession(), null, null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

}

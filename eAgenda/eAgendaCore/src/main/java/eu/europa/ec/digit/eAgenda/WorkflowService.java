package eu.europa.ec.digit.eAgenda;

import eu.europa.ec.digit.athena.workflow.WorkflowInstance;
import eu.europa.ec.digit.athena.workflow.service.AbstractWorkflowService;

public class WorkflowService extends AbstractWorkflowService<Object> {

	@Override
	public boolean apply(WorkflowInstance workflowInstance, String action, Object payload) {
		return super.apply(workflowInstance, action, payload);
	}

}

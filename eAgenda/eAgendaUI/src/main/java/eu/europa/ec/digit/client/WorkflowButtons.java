package eu.europa.ec.digit.client;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.athena.workflow.FiniteStateMachine;
import eu.europa.ec.digit.eAgenda.Appointment;

public class WorkflowButtons extends HorizontalPanel {

	public WorkflowButtons(Appointment appointment, String workflowName, FiniteStateMachine fsm) {
		addButtons(appointment, workflowName, fsm);
	}

	private void addButtons(Appointment appointment, String workflowName, FiniteStateMachine fsm) {
		List<String> actions = getActions(appointment, fsm); 
		for (String action:actions) {
			PushButton pbAction = new PushButton(action);
			add(pbAction);
			pbAction.addClickHandler(e -> {
				eAgendaUI.service.applyAction(workflowName, fsm, appointment, action, new RPCCallback<Appointment>() {

					@Override
					protected void onResult(Appointment result) {
						clear();
						addButtons(result, workflowName, fsm);
					}
				});
			});
		}
		
	}
	
	private List<String> getActions(Appointment a, FiniteStateMachine workflow) {
		return workflow.getTransitions().stream().filter(t -> t.currentState.equals(a.state == null ? "invited" : a.state)).map(t -> t.input).collect(Collectors.toList());
	}

}

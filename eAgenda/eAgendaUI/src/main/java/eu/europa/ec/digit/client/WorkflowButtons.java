package eu.europa.ec.digit.client;

import java.util.List;
import java.util.stream.Collectors;

import com.ceres.dynamicforms.client.components.EnabledVerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.athena.workflow.FiniteStateMachine;
import eu.europa.ec.digit.eAgenda.Appointment;

public class WorkflowButtons extends EnabledVerticalPanel {
	
	private EnabledVerticalPanel vpButtons = new EnabledVerticalPanel();
	private PopupPanel popup = new PopupPanel(true, true);

//	private FocusPanel vpButtonsWrapper = new FocusPanel(vpButtons);

	public WorkflowButtons(Appointment appointment, String workflowName, FiniteStateMachine fsm) {
//		vpButtonsWrapper.addMouseOutHandler(e -> showButtons(false));
		popup.add(vpButtons);
		addButtons(appointment, workflowName, fsm);
	}

	private void addButtons(Appointment appointment, String workflowName, FiniteStateMachine fsm) {
		popup.hide();
		vpButtons.clear();
		String state = appointment.getState(workflowName, fsm.initial);
		StateLabel stateLabel = new StateLabel(workflowName, state, fsm.isInitial(state), fsm.isInitial(state));
		add(stateLabel);
		List<String> actions = getActions(appointment, workflowName, fsm); 
		if (!actions.isEmpty()) {
			stateLabel.addStyleName("clickable");
			if (actions.size() == 1) {
				stateLabel.addClickHandler(getClickHandler(appointment, workflowName, fsm, actions.get(0)));
			} else {
				stateLabel.addClickHandler(e -> toggleButtons());
//				stateLabel.addMouseOverHandler(e -> showButtons(true));
				for (String action:actions) {
					PushButton pbAction = new PushButton(action);
					vpButtons.add(pbAction);
					pbAction.addClickHandler(getClickHandler(appointment, workflowName, fsm, action));
				}
			}
		}
		
	}
	
	
	private void toggleButtons() {
		if (popup.isShowing()) {
			popup.hide();
		} else {
			popup.showRelativeTo(this);
		}
//		
//		if (vpButtons.isAttached()) {
//			RootLayoutPanel.get().remove(vpButtons);
//		} else {
//			RootLayoutPanel rp = RootLayoutPanel.get();
//			int x = getAbsoluteLeft();
//			int y = getAbsoluteTop() + getOffsetHeight();
//			rp.add(vpButtons);
//			rp.setWidgetLeftWidth(vpButtons, x, Unit.PX, 3, Unit.EM);
//			rp.setWidgetTopHeight(vpButtons, y, Unit.PX, 10, Unit.EM);
//		}
	}

	private ClickHandler getClickHandler(Appointment appointment, String workflowName, FiniteStateMachine fsm, String action) {
		return e -> {
			eAgendaUI.service.applyAction(workflowName, fsm, appointment, action, new RPCCallback<Appointment>() {

				@Override
				protected void onResult(Appointment result) {
					clear();
					addButtons(result, workflowName, fsm);
				}
			});
		};
	}

//	private void showButtons(boolean b) {
//		if (b && !vpButtonsWrapper.isAttached()) {
//			int x = getAbsoluteLeft();
//			int y = getAbsoluteTop() + getOffsetHeight();
//			RootLayoutPanel rp = RootLayoutPanel.get();
//			rp.add(vpButtonsWrapper);
//			rp.setWidgetLeftWidth(vpButtonsWrapper, x, Unit.PX, 3, Unit.EM);
//			rp.setWidgetTopHeight(vpButtonsWrapper, y, Unit.PX, 10, Unit.EM);
//		} else if (vpButtonsWrapper.isAttached()) {
//			RootLayoutPanel.get().remove(vpButtonsWrapper);
//		}
//	}

	private List<String> getActions(Appointment a, String workflowName, FiniteStateMachine fsm) {
//		List<String> actions = new ArrayList<>();
//		String aState = a.getState(workflowName, fsm.initial);
//		for (FSMTransition t:fsm.getTransitions()) {
//			if (t.currentState.equals(aState)) {
//				actions.add(t.input);
//			}
//		}
//		return actions;
		return fsm.getTransitions().stream().filter(t -> t.currentState.equals(a.getState(workflowName, fsm.initial))).map(t -> t.input).collect(Collectors.toList());
	}

}

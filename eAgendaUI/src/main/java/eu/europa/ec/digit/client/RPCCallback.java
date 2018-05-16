package eu.europa.ec.digit.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RPCCallback<T> implements AsyncCallback<T> {
	
	public static final class NOP<T> extends RPCCallback<T> {

		@Override
		protected void onResult(T result) {
		}
	};

	public RPCCallback() {
		eAgendaUI.startBusy();
	}
	
	@Override
	public void onFailure(Throwable caught) {
		eAgendaUI.onFailure(caught);
	}

	@Override
	public void onSuccess(T result) {
		eAgendaUI.onSuccess();
		onResult(result);
	}

	protected abstract void onResult(T result);

}

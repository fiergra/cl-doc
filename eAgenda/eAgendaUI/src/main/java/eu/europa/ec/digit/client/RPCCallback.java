package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.MessageBox;
import com.ceres.dynamicforms.client.MessageBox.MESSAGE_ICONS;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;

import eu.europa.ec.digit.client.i18n.StringResources;

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
		if (caught instanceof InvocationException) {
			MessageBox.show(StringResources.getLabel("Error"), "Your session has expired, please login again", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_ERROR, r -> Window.Location.reload());
		} else {
			eAgendaUI.onFailure(caught);
		}
	}

	@Override
	public void onSuccess(T result) {
		eAgendaUI.onSuccess();
		onResult(result);
	}

	protected abstract void onResult(T result);

}

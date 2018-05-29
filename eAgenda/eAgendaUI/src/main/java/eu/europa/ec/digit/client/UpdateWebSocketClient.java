package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

import eu.europa.ec.digit.client.websocket.MessageEvent;
import eu.europa.ec.digit.client.websocket.WebSocket;
import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.shared.IAppointmentAction;
import eu.europa.ec.digit.shared.IAppointmentAction.ActionType;
import eu.europa.ec.digit.shared.WebSocketNotification;

public class UpdateWebSocketClient {

	private final WebSocket socket;
	private IAppointmentAction callback;
	private boolean socketOpened = false;

	private List<Appointment> appointments = new ArrayList<>();
	private IResource host;
//	private Date date;
	
	public UpdateWebSocketClient() {
		String url = GWT.getHostPageBaseURL();
		int index = url.indexOf("://");
		url = "ws" + url.substring(index) + "appointments";
		socket = new WebSocket(url);

		socket.onmessage = (evt) -> {
			MessageEvent event = evt.cast();
			if (callback != null) {
				String data = event.getData();
				processMessage(data);
			}
			return evt;
		};

		socket.onopen = (evt) -> {
			GWT.log("socket open");
			socketOpened = true;
			return evt;
		};

		socket.onclose = (evt) -> {
			GWT.log("socket closed");
			socketOpened = false;
			return evt;
		};

	}

	private void processMessage(String data) {
		WebSocketNotification n = deserialize(data);
		GWT.log(n.actionType.name());
		
		if (n.actionType.equals(ActionType.subscribe)) {
		} else {
			Appointment a = n.appointment;
			
			switch (n.actionType) {
			case update:break;
			case insert:
				if (a.host.equals(host)) {
					appointments.add(a);
					callback.action(n.actionType, a);
				}
				break;
			case delete:
				if (appointments.remove(a)) {
					callback.action(n.actionType, a);
				}
				break;
			case subscribe:
				break;
			case keepAlive:
				break;
			default:
				break;
			}
		}

	}

	public void subscribe(IResource host, Date d, IAppointmentAction iAction) {
		if (socketOpened) {
			this.host = host;
//			this.date = d;
			this.callback = iAction;
			Appointment a = new Appointment(host, null, null, d, null, null);
			WebSocketNotification wsn = new WebSocketNotification(ActionType.subscribe, a);
			String s = serialize(wsn);
			socket.send(s);
		} else {
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

				@Override
				public boolean execute() {
					subscribe(host, d, iAction);
					return !socketOpened;
				}
			}, 250);
		}
	}

	public void unsubscribe() {
	}

	private static final SerializationStreamFactory factory = (SerializationStreamFactory) GWT.create(GWTeAgendaService.class);

	private <T> String serialize(T t) {
		try {
			SerializationStreamWriter writer = factory.createStreamWriter();
			writer.writeObject(t);
			final String data = writer.toString();
			return data;
		} catch (final SerializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T> T deserialize(String data) {
		try {
			final SerializationStreamReader streamReader = factory.createStreamReader(data);
			@SuppressWarnings("unchecked")
			final T object = (T) streamReader.readObject();
			return object;
		} catch (final SerializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments.clear();
		this.appointments.addAll(appointments);
	}

}

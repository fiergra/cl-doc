package eu.europa.ec.digit.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.websocket.jsr356.JsrAsyncRemote;
import org.eclipse.jetty.websocket.jsr356.JsrSession;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.shared.IAppointmentAction.ActionType;
import eu.europa.ec.digit.shared.WebSocketNotification;


@ServerEndpoint("/appointments")
public class UpdateWebSocketServer {

	private static Set<Session> sessions = new HashSet<>();
	
	static {
		keepAlive();
	}

	public UpdateWebSocketServer() {
		System.out.println(sessions.size() + " open sessions");
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("WebSocket opened: " + session.getId());
		// JsrAsyncRemote ar = (JsrAsyncRemote) session.getAsyncRemote();
		// ar.sendText(sessions.size() + " open sessions");
		sessions.add(session);
		System.out.println(sessions.size() + " open sessions");
		
	}

	private static void keepAlive() {
		Thread t = new Thread(() -> {
			System.out.println("keep " + sessions.size() + " session(s) alive.");
			notifyAll(ActionType.keepAlive, null);
			try {
				Thread.sleep(60L * 1000L);
				keepAlive();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t.start();
	}

	@OnMessage
	public void onMessage(String txt, Session session) throws IOException, SerializationException {
		WebSocketNotification wsn = deserialize(txt);
		System.out.println("Message received: " + wsn);
	}

	static class Subscription {
		static int counter;

		final int id = ++counter;
		final Session s;

		Subscription(Session s) {
			this.s = s;
		}

	}

	private HashMap<String, HashMap<Date, Collection<Subscription>>> byHost = new HashMap<>();

	private Subscription addListener(Session session, String host, Date date) {
		HashMap<Date, Collection<Subscription>> bh = byHost.get(host);

		if (bh == null) {
			bh = new HashMap<>();
			byHost.put(host, bh);
		}

		Collection<Subscription> bd = bh.get(date);
		if (bd == null) {
			bd = new HashSet<>();
			bh.put(date, bd);
		}

		Subscription subscription = new Subscription(session);
		bd.add(subscription);
		return subscription;
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
		sessions.remove(session);
		System.out.println(sessions.size() + " open sessions");
	}

	public static void notifyAll(ActionType aType, Appointment a) {
		sessions.forEach(s -> {
			WebSocketNotification n = new WebSocketNotification(aType, a);
			try {
				String text = serialize(n);
				sendText(s, text);
			} catch (SerializationException e) {
				e.printStackTrace();
			}
			
//			TypeAdapter<WebSocketNotification> adapter = gson.getAdapter(WebSocketNotification.class);
//			String json = adapter.toJson(n);


		});
	}

	
	private static void sendText(Session session, String text) {
		if (session instanceof JsrSession) {
			JsrAsyncRemote ar = (JsrAsyncRemote) session.getAsyncRemote();
			ar.sendText(text);
		} else {
			Async ar = session.getAsyncRemote();
			ar.sendText(text);
		}
	}

	private static <T> String serialize(final T a) throws SerializationException {
		ServerSerializationStreamWriter serverSerializationStreamWriter = new ServerSerializationStreamWriter(new SimpleSerializationPolicy());
		serverSerializationStreamWriter.writeObject(a);
		String result = serverSerializationStreamWriter.toString();
		return result;
	}

	private static WebSocketNotification dummy = new WebSocketNotification(null, null);
	
	private static <T> T deserialize(String data) throws SerializationException {
		ServerSerializationStreamReader streamReader = new ServerSerializationStreamReader(WebSocketNotification.class.getClassLoader(), new CustomSerializationPolicyProvider());
//		ServerSerializationStreamReader streamReader = new ServerSerializationStreamReader(Thread.currentThread().getContextClassLoader(), new CustomSerializationPolicyProvider());
		// Filling stream reader with data
		streamReader.prepareToRead(data);
		// Reading deserialized object from the stream
		final T a = (T) streamReader.readObject();
		return a;
	}

}

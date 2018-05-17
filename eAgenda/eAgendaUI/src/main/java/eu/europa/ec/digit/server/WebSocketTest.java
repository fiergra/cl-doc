package eu.europa.ec.digit.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

public class WebSocketTest {

	public static void main(String[] args) throws DeploymentException, IOException, URISyntaxException, InterruptedException {
		String dest = "ws://localhost:8888/appointments";
		UpdateWebSocketClient socket = new UpdateWebSocketClient();
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(socket, new URI(dest));

		socket.getLatch().await();
		socket.sendMessage("{ \"actionType\": \"unsubscribe\", \"id\":" + 123 + "}");
		Thread.sleep(10000l);
	}

}

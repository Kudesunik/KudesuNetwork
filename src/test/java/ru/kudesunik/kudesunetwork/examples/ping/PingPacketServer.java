package ru.kudesunik.kudesunetwork.examples.ping;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;

public class PingPacketServer {
	
	private final PingServerNetworkListener handler;
	private final NetworkParameters parameters;
	
	public PingPacketServer() {
		this.handler = new PingServerNetworkListener();
		this.parameters = new NetworkParameters();
		this.parameters.setAuthorization("Login", "Password");
	}
	
	private void start() {
		KudesuNetworkServer server = KudesuNetwork.createServer(8888, null, handler, parameters);
		server.start();
		try {
			Thread.sleep(30000);
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		server.stop();
	}
	
	public static void main(String[] args) {
		(new PingPacketServer()).start();
	}
}

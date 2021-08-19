package ru.kudesunik.kudesunetwork.examples.custompacket;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;

public class CustomPacketServer {
	
	private final CustomPacketRegistrator registrator;
	private final CustomServerNetworkListener handler;
	private final NetworkParameters parameters;
	
	public CustomPacketServer() {
		this.registrator = new CustomPacketRegistrator();
		this.handler = new CustomServerNetworkListener();
		this.parameters = new NetworkParameters();
		this.parameters.setAuthorization("Login", "Password");
	}
	
	private void start() {
		KudesuNetworkServer server = KudesuNetwork.createServer(8888, registrator, handler, parameters);
		server.start();
		try {
			Thread.sleep(30000);
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		server.stop();
	}
	
	public static void main(String[] args) {
		(new CustomPacketServer()).start();
	}
}

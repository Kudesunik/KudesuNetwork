package ru.kudesunik.kudesocket.examples.rawpacket;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.NetworkParameters;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;

public class RawPacketServer {
	
	private final RawServerNetworkListener handler;
	private final NetworkParameters parameters;
	
	public RawPacketServer() {
		this.handler = new RawServerNetworkListener();
		this.parameters = new NetworkParameters();
	}
	
	private void start() {
		KudesuNetworkServer server = KudesuNetwork.createServer(8888, null, handler, parameters, false);
		server.start();
		try {
			Thread.sleep(20000);
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		server.stop();
	}
	
	public static void main(String[] args) {
		(new RawPacketServer()).start();
	}
}

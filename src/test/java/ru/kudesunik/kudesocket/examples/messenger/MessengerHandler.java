package ru.kudesunik.kudesocket.examples.messenger;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.NetworkParameters;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class MessengerHandler {
	
	private final PacketRegistrator registrator;
	private final NetworkServerListener serverListener;
	private final NetworkClientListener clientListener;
	private final NetworkParameters parameters;
	
	private KudesuNetworkServer server;
	private KudesuNetworkClient client;
	
	public MessengerHandler() {
		this.registrator = new MessengerPacketRegistrator();
		this.serverListener = new MessengerServerNetworkListener(this);
		this.clientListener = new MessengerClientNetworkListener(this);
		this.parameters = new NetworkParameters();
		setupParameters();
	}
	
	private void setupParameters() {
		parameters.setSendHandshake(true);
	}
	
	public boolean connectToServer(String address, int port, String login, String password) {
		if(client == null) {
			client = KudesuNetwork.createClient(address, port, registrator, clientListener, parameters);
			if(client.connect()) {
				client.sendPacket(new Packet10Login(login, password));
				return true;
			}
		}
		return false;
	}
	
	public void disconnectFromServer() {
		if(client != null) {
			if(client.isAlive()) {
				client.disconnect();
			}
			client = null;
		}
	}
	
	public boolean startServer(int port) {
		if(server == null) {
			server = KudesuNetwork.createServer(port, registrator, serverListener, parameters);
			return server.start();
		} else {
			return false;
		}
	}
	
	public void stopServer() {
		if(server != null) {
			if(server.isAlive()) {
				server.stop();
			}
			server = null;
		}
	}
	
	public boolean isServerWorking() {
		return (server != null);
	}
	
	public boolean isClientConnected() {
		return (client != null);
	}
}

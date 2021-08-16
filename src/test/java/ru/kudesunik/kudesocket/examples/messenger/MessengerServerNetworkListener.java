package ru.kudesunik.kudesocket.examples.messenger;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class MessengerServerNetworkListener implements NetworkServerListener {
	
	private KudesuNetworkServer server;
	
	public MessengerServerNetworkListener(MessengerHandler messengerHandler) {
		
	}
	
	@Override
	public void bind(KudesuNetworkServer server) {
		this.server = server;
	}
	
	@Override
	public boolean onConnection(InetAddress address, int port, int localPort) {
		return true;
	}
	
	@Override
	public boolean onHandshake(int port, String protocolName, int protocolVersion) {
		return protocolName.equals(KudesuNetwork.PROTOCOL_NAME) && (KudesuNetwork.PROTOCOL_VERSION == protocolVersion);
	}
	
	@Override
	public boolean onAuthorization(int port, String data) {
		return data.equals(server.getParameters().getAuthorization());
	}
	
	@Override
	public void onPacketReceive(int port, Packet packet) {
		
	}
	
	@Override
	public void onPing(int port, int count) {
		//Not yet done
	}
	
	@Override
	public void onDisconnection(int port) {
		
	}
}

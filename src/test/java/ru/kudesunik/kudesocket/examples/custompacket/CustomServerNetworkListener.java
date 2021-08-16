package ru.kudesunik.kudesocket.examples.custompacket;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class CustomServerNetworkListener implements NetworkServerListener {
	
	private KudesuNetworkServer server;
	
	private void handleCustomPacket(CustomPacket packet, int port) {
		System.out.println("Received packet with id: " + packet.getId() + ", 1: " + packet.getCustomNumber1() + ", 2: " + packet.getCustomNumber2());
		//Do funny XOR packet values swap
		packet.setCustomNumber1(packet.getCustomNumber1() ^ packet.getCustomNumber2());
		packet.setCustomNumber2(packet.getCustomNumber2() ^ packet.getCustomNumber1());
		packet.setCustomNumber1(packet.getCustomNumber1() ^ packet.getCustomNumber2());
		server.sendPacket(packet, port);
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
		switch(packet.getId()) {
		case CustomPacket.ID:
			handleCustomPacket((CustomPacket) packet, port);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + packet.getId());
		}
	}
	
	@Override
	public void onPing(int port, int count) {
		
	}
	
	@Override
	public void onDisconnection(int port) {
		
	}
}

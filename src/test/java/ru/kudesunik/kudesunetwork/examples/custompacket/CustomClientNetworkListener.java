package ru.kudesunik.kudesunetwork.examples.custompacket;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

public class CustomClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
	private void handleCustomPacket(CustomPacket packet) {
		System.out.println("Received packet with id: " + packet.getId() + ", 1: " + packet.getCustomNumber1() + ", 2: " + packet.getCustomNumber2());
	}
	
	@Override
	public void bind(KudesuNetworkClient client) {
		this.client = client;
	}
	
	@Override
	public boolean onConnection(boolean isConnected) {
		return isConnected;
	}
	
	@Override
	public boolean onHandshake(String protocolName, int protocolVersion) {
		return protocolName.equals(KudesuNetwork.PROTOCOL_NAME) && (KudesuNetwork.PROTOCOL_VERSION == protocolVersion);
	}
	
	@Override
	public boolean onAuthorization(String data) {
		return data.equals(client.getParameters().getAuthorization());
	}
	
	@Override
	public void onPacketReceive(Packet packet) {
		switch(packet.getId()) {
		case CustomPacket.ID:
			handleCustomPacket((CustomPacket) packet);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + packet.getId());
		}
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		return true;
	}
	
	@Override
	public void onDisconnection(int reason) {
		
	}

	@Override
	public void receiveProgress(int packetId, int totalSize, int currentSize) {
		//No use
	}
}

package ru.kudesunik.kudesunetwork.examples.rawpacket;

import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;

public class RawClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
	private void handleRawPacket(Packet4Raw packet) {
		byte[] data = packet.getData();
		System.out.print("Raw packet received: ");
		for(int i = 0; i < data.length; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
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
		return false; //No use with raw packet
	}
	
	@Override
	public boolean onAuthorization(String data) {
		return false; //No use with raw packet
	}
	
	@Override
	public void onPacketReceive(Packet packet) {
		if(packet.getId() == Packet4Raw.ID) {
			handleRawPacket((Packet4Raw) packet);
		}
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		return false; //No use with raw packet
	}
	
	@Override
	public void onDisconnection(int reason) {
		//No use
	}
	
	@Override
	public void receiveProgress(int packetId, int totalSize, int currentSize) {
		//No use
	}
}

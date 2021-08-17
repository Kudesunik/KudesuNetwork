package ru.kudesunik.kudesocket.examples.rawpacket;

import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;

public class RawClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
	private void handleRawPacket(Packet4Raw packet) {
		System.out.println("Raw packet received!");
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
		if(packet instanceof Packet4Raw) {
			handleRawPacket((Packet4Raw) packet);
		}
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		return false; //No use with raw packet
	}
	
	@Override
	public void onDisconnection() {
		
	}
}

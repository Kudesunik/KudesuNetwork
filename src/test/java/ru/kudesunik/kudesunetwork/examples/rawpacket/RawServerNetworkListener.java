package ru.kudesunik.kudesunetwork.examples.rawpacket;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class RawServerNetworkListener implements NetworkServerListener {
	
	private KudesuNetworkServer server;
	
	private void handleRawPacket(Packet4Raw packet) {
		byte[] data = packet.getData();
		System.out.print("Raw packet received: ");
		for(int i = 0; i < data.length; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
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
		return false; //No use with raw packet
	}
	
	@Override
	public boolean onAuthorization(int port, String data) {
		return false; //No use with raw packet
	}
	
	@Override
	public void onPacketReceive(int port, Packet packet) {
		if(packet instanceof Packet4Raw) {
			handleRawPacket((Packet4Raw) packet);
		}
	}
	
	@Override
	public boolean onPing(int port, long id, long sendedTimestamp, long receivedTimestamp) {
		return false; //No use with raw packet
	}
	
	@Override
	public void onDisconnection(int port, int reason) {
		//No use
	}
	
	@Override
	public void receiveProgress(int packetId, int totalSize, int currentSize) {
		//No use
	}
}

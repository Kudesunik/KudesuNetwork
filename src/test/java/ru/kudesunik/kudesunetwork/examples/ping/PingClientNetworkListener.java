package ru.kudesunik.kudesunetwork.examples.ping;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

public class PingClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
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
		System.out.println("Packet received: " + packet.getId() + ", this is not good!"); //Nothing should happen
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		long ping = (receivedTimestamp - sendedTimestamp) / 2;
		StringBuilder sb = new StringBuilder();
		sb.append("Ping received: ");
		sb.append(id);
		sb.append("; Sended: ");
		sb.append(sendedTimestamp);
		sb.append("; Received: ");
		sb.append(receivedTimestamp);
		sb.append("; Ping: ");
		sb.append(ping / 1000000);
		sb.append(" ms (");
		sb.append(ping / 1000000.0f);
		sb.append(" ms)");
		System.out.println(sb.toString());
		return true;
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

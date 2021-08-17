package ru.kudesunik.kudesocket.examples.messenger;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

public class MessengerClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
	private MessengerHandler handler;
	
	public MessengerClientNetworkListener(MessengerHandler handler) {
		this.handler = handler;
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
		
		default:
			break;
		}
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		return true;
	}
	
	@Override
	public void onDisconnection() {
		
	}
}

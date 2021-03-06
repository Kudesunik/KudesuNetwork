package ru.kudesunik.kudesunetwork.client;

import ru.kudesunik.kudesunetwork.handler.NetworkListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

public interface NetworkClientListener extends NetworkListener {
	
	public void bind(KudesuNetworkClient client);
	
	public boolean onConnection(boolean isConnected);
	
	public boolean onHandshake(String protocolName, int protocolVersion);
	
	public boolean onAuthorization(String data);
	
	public void onPacketReceive(Packet packet);
	
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp);
	
	public void onDisconnection(int reason);
}

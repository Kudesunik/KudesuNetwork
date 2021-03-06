package ru.kudesunik.kudesunetwork.server;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.annotations.ServerSide;
import ru.kudesunik.kudesunetwork.handler.NetworkListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

/**
 * Server network listener interface;
 * <br>Remember that when you receive events using this interface, you are using the network library threads. 
 * Use your own threads if required.
 * @author Kudesunik
 *
 */
@ServerSide
public interface NetworkServerListener extends NetworkListener {
	
	public void bind(KudesuNetworkServer server);
	
	public boolean onConnection(InetAddress address, int port, int localPort);
	
	public boolean onHandshake(int port, String protocolName, int protocolVersion);
	
	public boolean onAuthorization(int port, String data);
	
	public void onPacketReceive(int port, Packet packet);
	
	public boolean onPing(int port, long id, long sendedTimestamp, long receivedTimestamp);
	
	public void onDisconnection(int port, int reason);
}

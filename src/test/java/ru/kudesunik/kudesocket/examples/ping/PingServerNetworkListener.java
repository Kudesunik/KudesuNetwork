package ru.kudesunik.kudesocket.examples.ping;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class PingServerNetworkListener implements NetworkServerListener {
	
	private KudesuNetworkServer server;
	
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
		System.out.println("Packet received: " + packet.getId() + ", this is not good!"); //Nothing should happen
	}
	
	@Override
	public boolean onPing(int port, long id, long sendedTimestamp, long receivedTimestamp) {
		long ping = (receivedTimestamp - sendedTimestamp) / 2;
		StringBuilder sb = new StringBuilder();
		sb.append("Ping received: ");
		sb.append(id);
		sb.append(" from port: ");
		sb.append(port);
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
	public void onDisconnection(int port) {
		
	}
}

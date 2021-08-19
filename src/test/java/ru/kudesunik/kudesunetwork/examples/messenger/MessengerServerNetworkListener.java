package ru.kudesunik.kudesunetwork.examples.messenger;

import java.net.InetAddress;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.annotations.ServerSide;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

@ServerSide
public class MessengerServerNetworkListener implements NetworkServerListener {
	
	private final MessengerHandler messengerHandler;
	
	private KudesuNetworkServer server;
	
	public MessengerServerNetworkListener(MessengerHandler messengerHandler) {
		this.messengerHandler = messengerHandler;
	}
	
	private void handleLoginPacket(int port, Packet10Login loginPacket) {
		messengerHandler.handleLogin(port, loginPacket.getLogin(), loginPacket.getPassword());
	}
	
	private void handleMessagePacket(int port, Packet12Message messagePacket) {
		messengerHandler.handleMessage(port, messagePacket.getMessage().getText());
	}
	
	private void handleImagePacket(int port, Packet13Image imagePacket) {
		messengerHandler.handleImage(port, imagePacket);
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
		case Packet10Login.ID:
			handleLoginPacket(port, (Packet10Login) packet);
			break;
		case Packet12Message.ID:
			handleMessagePacket(port, (Packet12Message) packet);
			break;
		case Packet13Image.ID:
			handleImagePacket(port, (Packet13Image) packet);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onPing(int port, long id, long sendedTimestamp, long receivedTimestamp) {
		return true;
	}
	
	@Override
	public void onDisconnection(int port, int reason) {
		messengerHandler.handleDisconnection(port, reason);
	}
	
	@Override
	public void receiveProgress(int packetId, int totalSize, int currentSize) {
		if(totalSize > KudesuNetwork.MAX_DATA_SIZE) {
			System.out.println("Big packet receive progress id: " + packetId + "; Size: " + currentSize + " of " + totalSize + " bytes");
		}
	}
}

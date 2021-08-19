package ru.kudesunik.kudesocket.examples.messenger;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;

public class MessengerClientNetworkListener implements NetworkClientListener {
	
	private KudesuNetworkClient client;
	
	private MessengerHandler messengerHandler;
	
	public MessengerClientNetworkListener(MessengerHandler handler) {
		this.messengerHandler = handler;
	}
	
	private void handleUserPacket(Packet11User userPacket) {
		messengerHandler.handleUser(userPacket.isConnected(), userPacket.getLogin());
	}
	
	private void handleMessagePacket(Packet12Message messagePacket) {
		Message message = messagePacket.getMessage();
		messengerHandler.handleMessage(message.getLogin(), message.getText());
	}
	
	private void handleImagePacket(Packet13Image imagePacket) {
		messengerHandler.handleImage(imagePacket.getImage());
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
		case Packet11User.ID:
			handleUserPacket((Packet11User) packet);
			break;
		case Packet12Message.ID:
			handleMessagePacket((Packet12Message) packet);
			break;
		case Packet13Image.ID:
			handleImagePacket((Packet13Image) packet);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onPing(long id, long sendedTimestamp, long receivedTimestamp) {
		return true;
	}
	
	@Override
	public void onDisconnection(int reason) {
		messengerHandler.handleDisconnection(reason);
	}

	@Override
	public void receiveProgress(int packetId, int totalSize, int currentSize) {
		
	}
}

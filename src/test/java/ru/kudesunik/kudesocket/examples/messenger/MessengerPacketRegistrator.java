package ru.kudesunik.kudesocket.examples.messenger;

import java.util.ArrayList;
import java.util.List;

import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;

public class MessengerPacketRegistrator implements PacketRegistrator {
	
	private final Packet10Login loginPacket;
	private final Packet11User userPacket;
	private final Packet12Message messagePacket;
	private final Packet13Image imagePacket;
	
	public MessengerPacketRegistrator() {
		this.loginPacket = new Packet10Login();
		this.userPacket = new Packet11User();
		this.messagePacket = new Packet12Message();
		this.imagePacket = new Packet13Image();
	}
	
	@Override
	public List<Packet> registerPackets() {
		List<Packet> packets = new ArrayList<>();
		packets.add(loginPacket);
		packets.add(userPacket);
		packets.add(messagePacket);
		packets.add(imagePacket);
		return packets;
	}
}
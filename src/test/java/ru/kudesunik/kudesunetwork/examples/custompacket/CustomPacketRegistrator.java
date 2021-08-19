package ru.kudesunik.kudesunetwork.examples.custompacket;

import java.util.ArrayList;
import java.util.List;

import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;

public class CustomPacketRegistrator implements PacketRegistrator {
	
	private final CustomPacket customPacket;
	
	public CustomPacketRegistrator() {
		this.customPacket = new CustomPacket();
	}
	
	@Override
	public List<Packet> registerPackets() {
		List<Packet> packets = new ArrayList<>();
		packets.add(customPacket);
		return packets;
	}
}

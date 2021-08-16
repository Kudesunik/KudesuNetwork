package ru.kudesunik.kudesunetwork;

import java.util.List;

import org.apache.logging.log4j.Level;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import ru.kudesunik.kudesunetwork.annotations.Nullable;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;

public abstract class NetworkBase {
	
	private final int port;
	private final NetworkParameters parameters;
	private boolean useProtocol;
	
	private final Int2ObjectMap<Packet> packets;
	
	protected boolean isWorking;
	
	protected NetworkBase(int port, PacketRegistrator registrator, NetworkParameters parameters, boolean useProtocol) {
		this.port = port;
		this.parameters = parameters;
		this.useProtocol = useProtocol;
		this.packets = new Int2ObjectArrayMap<>();
		this.isWorking = true;
		registerPackets(registrator);
	}
	
	private void registerPackets(PacketRegistrator registrator) {
		KudesuNetwork.getProtocolPackets().forEach(packet -> registerPacket(packet, true));
		if(registrator != null) {
			List<Packet> customPackets = registrator.registerPackets();
			if(!customPackets.isEmpty()) {
				for(int i = 0; i < customPackets.size(); i++) {
					registerPacket(customPackets.get(i), false);
				}
				KudesuNetwork.log(Level.INFO, "Registered " + packets.size() + " network packets (with " + KudesuNetwork.getProtocolPackets().size() + " protocol packets)");
			}
		}
	}
	
	private void registerPacket(Packet packet, boolean bypassIdCheck) {
		int packetId = packet.getId();
		if((packetId < 10) && !bypassIdCheck) {
			KudesuNetwork.log(Level.ERROR, "Packet id must be >= 10 due to reserved packets");
			return;
		}
		if(!packets.containsKey(packetId)) {
			packets.put(packetId, packet);
		} else {
			KudesuNetwork.log(Level.ERROR, "Packet with that id already exists");
		}
	}
	
	public boolean isPacketExist(int packetId) {
		return packets.containsKey(packetId);
	}
	
	public boolean isProtocolPacket(int packetId) {
		return KudesuNetwork.isProtocolPacket(packetId);
	}
	
	public @Nullable Packet getPacketContainer(int packetId) {
		try {
			return packets.get(packetId).clone();
		} catch(CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public int getPort() {
		return port;
	}
	
	public NetworkParameters getParameters() {
		return parameters;
	}
	
	public boolean useProtocol() {
		return useProtocol;
	}
	
	public abstract void onConnectionDropped(int port);
	
	public abstract boolean isAlive();
}

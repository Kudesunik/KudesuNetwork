package ru.kudesunik.kudesunetwork;

import java.util.List;

import org.apache.logging.log4j.Level;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import ru.kudesunik.kudesunetwork.annotations.Nullable;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.util.NetworkCipher;

public abstract class NetworkBase {
	
	public static final int SEVERE_DISCONNECTION = 0;
	public static final int NORMAL_DISCONNECTION = 1;
	
	private final int port;
	private final NetworkParameters parameters;
	private boolean useProtocol;
	
	private final Int2ObjectMap<Packet> packets;
	
	private final NetworkCipher cipher;
	
	protected boolean isWorking;
	
	protected NetworkBase(int port, PacketRegistrator registrator, NetworkParameters parameters, boolean useProtocol) {
		this.port = port;
		this.parameters = parameters;
		this.useProtocol = useProtocol;
		this.packets = new Int2ObjectArrayMap<>();
		this.isWorking = true;
		registerPackets(registrator);
		if(parameters.isEncrypt()) {
			String authorization = parameters.getAuthorization();
			if(authorization != null) {
				this.cipher = new NetworkCipher(authorization);
			} else {
				this.cipher = new NetworkCipher(NetworkCipher.SALT);
				KudesuNetwork.log(Level.INFO, "Not fully protected: authorization is disabled, default encryption will be applied");
			}
		} else {
			if(useProtocol) {
				KudesuNetwork.log(Level.INFO, "Very unsafe: encryption disabled, no data encryption will be applied");
			} else {
				KudesuNetwork.log(Level.INFO, "Raw data mode: no data stream encryption, encrypt by yourself");
			}
			cipher = null;
		}
	}
	
	public @Nullable NetworkCipher getCipher() {
		return cipher;
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

package ru.kudesunik.kudesunetwork;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import ru.kudesunik.kudesunetwork.annotations.NonNull;
import ru.kudesunik.kudesunetwork.annotations.PermanentAllocation;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.logger.LoggerConfigurationFactory;
import ru.kudesunik.kudesunetwork.logger.UncaughtExceptionHandler;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet1Handshake;
import ru.kudesunik.kudesunetwork.packet.Packet2Authorization;
import ru.kudesunik.kudesunetwork.packet.Packet3Ping;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;
import ru.kudesunik.kudesunetwork.util.Utilities;

public class KudesuNetwork {
	
	public static final Level LOGGER_LEVEL = Level.DEBUG;
	
	private static final Logger LOGGER;
	
	public static final String PROTOCOL_NAME = "KudesuNetwork";
	
	public static final short PROTOCOL_VERSION = 1;
	
	@PermanentAllocation
	private static final Int2ObjectMap<Packet> protocolPackets = new Int2ObjectArrayMap<>();
	
	public static KudesuNetworkServer createServer(int port, PacketRegistrator registrator, NetworkServerListener listener, NetworkParameters parameters, boolean useProtocol) {
		return new KudesuNetworkServer(port, registrator, listener, parameters, useProtocol);
	}
	
	public static KudesuNetworkServer createServer(int port, PacketRegistrator registrator, NetworkServerListener listener, NetworkParameters parameters) {
		return new KudesuNetworkServer(port, registrator, listener, parameters, true);
	}
	
	public static KudesuNetworkClient createClient(String address, int port, PacketRegistrator registrator, NetworkClientListener listener, NetworkParameters parameters, boolean useProtocol) {
		return new KudesuNetworkClient(address, port, registrator, listener, parameters, useProtocol);
	}
	
	public static KudesuNetworkClient createClient(String address, int port, PacketRegistrator registrator, NetworkClientListener listener, NetworkParameters parameters) {
		return new KudesuNetworkClient(address, port, registrator, listener, parameters, true);
	}
	
	public static @NonNull ObjectCollection<Packet> getProtocolPackets() {
		if(protocolPackets.isEmpty()) {
			protocolPackets.put(1, new Packet1Handshake());
			protocolPackets.put(2, new Packet2Authorization(null));
			protocolPackets.put(3, new Packet3Ping());
			protocolPackets.put(4, new Packet4Raw(null));
		}
		return protocolPackets.values();
	}
	
	public static boolean isProtocolPacket(int packetId) {
		return protocolPackets.containsKey(packetId);
	}
	
	public static void initializeLogger(boolean useConsoleLogging, boolean useFileLogging, Level level) {
		ConfigurationFactory.setConfigurationFactory(new LoggerConfigurationFactory(useConsoleLogging, useFileLogging, level));
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
	}
	
	public static void log(Level level, Object... log) {
		LOGGER.log(level, Utilities.concat(log));
	}
	
	private KudesuNetwork() {
		//Class instantiation not allowed
	}
	
	static {
		initializeLogger(true, true, LOGGER_LEVEL);
		LOGGER = LogManager.getRootLogger();
	}
}

package ru.kudesunik.kudesunetwork.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.NetworkBase;
import ru.kudesunik.kudesunetwork.annotations.ThreadSafe;
import ru.kudesunik.kudesunetwork.handler.NetworkHandler;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.util.NamedThreadFactory;

public class KudesuNetworkServer extends NetworkBase {
	
	private ServerSocket serverSocket;
	
	private final NetworkServerListener listener;
	
	private final ExecutorService connectionExecutorService;
	
	private final Int2ObjectMap<NetworkHandler> handlers;
	
	public KudesuNetworkServer(int port, PacketRegistrator registrator, NetworkServerListener listener, NetworkParameters parameters, boolean useProtocol) {
		super(port, registrator, parameters, useProtocol);
		this.listener = listener;
		this.listener.bind(this);
		this.connectionExecutorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("KudesuNetwork Connection", false, false));
		this.handlers = Int2ObjectMaps.synchronize(new Int2ObjectArrayMap<>());
	}
	
	public boolean start() {
		KudesuNetwork.log(Level.INFO, "Starting network server...");
		try {
			serverSocket = new ServerSocket(getPort());
		} catch(IOException ex) {
			ex.printStackTrace();
			return false;
		}
		connectionExecutorService.execute(() -> {
			while(isWorking) {
				try {
					startClient(serverSocket.accept()); //Blocking client connection accept operation
				} catch(SocketException ex) {
					isWorking = false;
					return;
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		KudesuNetwork.log(Level.INFO, "Network server started!");
		return true;
	}
	
	@ThreadSafe(callerThread = "KudeSocket Connection")
	private void startClient(Socket client) {
		if(client.isConnected()) {
			NetworkHandler clientHandler = null;
			try {
				clientHandler = new NetworkHandler(client, this, listener, getParameters(), useProtocol());
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			if(clientHandler != null) {
				handlers.put(client.getPort(), clientHandler);
				clientHandler.start();
			}
		}
	}
	
	@ThreadSafe(callerThread = "Unknown")
	public void sendPacket(Packet packet, int port) {
		if(port == 0) {
			sendBroadcast(packet);
		} else {
			NetworkHandler handler;
			handler = handlers.get(port);
			if(handler != null) {
				handler.sendPacket(packet);
			}
		}
	}
	
	@ThreadSafe(callerThread = "Unknown")
	public void sendBroadcast(Packet packet) {
		for(NetworkHandler handler : handlers.values()) {
			handler.sendPacket(packet);
		}
	}
	
	public int getConnectedClientsCount() {
		return handlers.size();
	}
	
	@Override
	public boolean isAlive() {
		return !serverSocket.isClosed();
	}
	
	/**
	 * Stops server and disconnects all clients normally;
	 * <br>This must be called to stop server (or program in general) cause connection listener thread is not daemon
	 */
	public void stop() {
		KudesuNetwork.log(Level.INFO, "Stopping server...");
		try {
			serverSocket.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		connectionExecutorService.shutdown();
		KudesuNetwork.log(Level.DEBUG, "Clients to disconnect: " + getConnectedClientsCount());
		if(getConnectedClientsCount() > 0) {
			for(NetworkHandler handler : handlers.values()) {
				handler.requestDropConnection();
			}
		}
		KudesuNetwork.log(Level.INFO, "Server stopped!");
	}
	
	@Override
	public void onConnectionDropped(int port) {
		if(handlers.remove(port) == null) {
			KudesuNetwork.log(Level.FATAL, "Dropped handler not exist: " + port);
			stop();
		}
	}
}

package ru.kudesunik.kudesunetwork.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.NetworkBase;
import ru.kudesunik.kudesunetwork.NetworkParameters;
import ru.kudesunik.kudesunetwork.handler.NetworkHandler;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;

public class KudesuNetworkClient extends NetworkBase {
	
	private final String address;
	
	private final NetworkClientListener listener;
	
	private NetworkHandler clientHandler;
	
	public KudesuNetworkClient(String address, int port, PacketRegistrator registrator, NetworkClientListener listener, NetworkParameters parameters, boolean useProtocol) {
		super(port, registrator, parameters, useProtocol);
		this.address = address;
		this.listener = listener;
		this.listener.bind(this);
	}
	
	public boolean connect() {
		KudesuNetwork.log(Level.INFO, "Starting network client...");
		try {
			return startClient(new Socket(address, getPort()));
		} catch(IOException ex) {
			KudesuNetwork.log(Level.ERROR, "Connection failed");
			if(KudesuNetwork.LOGGER_LEVEL == Level.DEBUG) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean startClient(Socket client) {
		if(client.isConnected()) {
			try {
				clientHandler = new NetworkHandler(client, this, listener, getParameters(), useProtocol());
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			boolean isConnected = (clientHandler != null);
			listener.onConnection(isConnected);
			if(isConnected) {
				clientHandler.start();
				KudesuNetwork.log(Level.INFO, "Network client started!");
				return true;
			}
		}
		return false;
	}
	
	public void sendPacket(Packet packet) {
		clientHandler.sendPacket(packet);
	}
	
	@Override
	public boolean isAlive() {
		return (clientHandler != null) && clientHandler.isAlive();
	}
	
	public void disconnect() {
		clientHandler.requestDropConnection();
	}
	
	@Override
	public void onConnectionDropped(int port) {
		//No use
	}
}

package ru.kudesunik.kudesunetwork.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.NetworkBase;
import ru.kudesunik.kudesunetwork.annotations.ThreadSafe;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet1Handshake;
import ru.kudesunik.kudesunetwork.packet.Packet2Authorization;
import ru.kudesunik.kudesunetwork.packet.Packet3Ping;
import ru.kudesunik.kudesunetwork.packet.Packet5Disconnect;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;
import ru.kudesunik.kudesunetwork.util.NamedThreadFactory;
import ru.kudesunik.kudesunetwork.util.task.TaskManager;

public class NetworkHandler {
	
	private static final int THREAD_TERMINATION_TIME = 1000;
	
	private static final String READER_THREAD_NAME = "KudesuNetwork Reader";
	private static final String WORKER_THREAD_NAME = "KudesuNetwork Worker";
	
	private static final NamedThreadFactory READER_FACTORY = new NamedThreadFactory(READER_THREAD_NAME, true);
	private static final NamedThreadFactory WORKER_FACTORY = new NamedThreadFactory(WORKER_THREAD_NAME, true);
	
	private final Socket socket;
	private final NetworkBase base;
	
	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	private NetworkClientListener clientListener;
	private NetworkServerListener serverListener;
	
	private final NetworkParameters parameters;
	
	private final boolean useProtocol;
	
	private final ExecutorService readerExecutorService;
	private final ExecutorService workerExecutorService;
	
	private final NetworkSide networkSide;
	
	private final NetworkReader networkReader;
	private final NetworkWorker networkWorker;
	
	private volatile boolean isNetworkReady;
	private volatile boolean isDisconnectPacketSended;
	private volatile boolean isDropConnectionCalled;
	
	public NetworkHandler(Socket socket, NetworkBase base, NetworkListener listener, NetworkParameters parameters, boolean useProtocol) throws IOException {
		this.socket = socket;
		this.base = base;
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
		if(listener instanceof NetworkClientListener) {
			this.clientListener = (NetworkClientListener) listener;
			this.networkSide = NetworkSide.CLIENT;
		} else {
			this.serverListener = (NetworkServerListener) listener;
			this.networkSide = NetworkSide.SERVER;
		}
		this.parameters = parameters;
		this.useProtocol = useProtocol;
		this.readerExecutorService = Executors.newSingleThreadExecutor(READER_FACTORY);
		this.workerExecutorService = Executors.newSingleThreadExecutor(WORKER_FACTORY);
		this.networkWorker = new NetworkWorker(this, useProtocol);
		this.networkReader = new NetworkReader(this, networkWorker, useProtocol);
		this.isNetworkReady = false;
		this.isDisconnectPacketSended = false;
		this.isDropConnectionCalled = false;
	}
	
	public void start() {
		readerExecutorService.execute(networkReader);
		workerExecutorService.execute(networkWorker);
		if(useProtocol) {
			if(parameters.getSendHandshake()) {
				sendPacket(new Packet1Handshake());
			}
			if(parameters.getAuthorization() != null) {
				sendPacket(new Packet2Authorization(parameters.getAuthorization()));
			}
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	public void receivePacket(Packet packet) {
		switch(packet.getId()) {
		case Packet1Handshake.ID:
			receiveHandshakePacket((Packet1Handshake) packet);
			break;
		case Packet2Authorization.ID:
			receiveAuthorizationPacket((Packet2Authorization) packet);
			break;
		case Packet3Ping.ID:
			receivePingPacket((Packet3Ping) packet);
			break;
		case Packet5Disconnect.ID:
			receiveDisconnectionPacket((Packet5Disconnect) packet);
			break;
		default:
			if(networkSide == NetworkSide.CLIENT) {
				clientListener.onPacketReceive(packet);
			} else {
				serverListener.onPacketReceive(socket.getPort(), packet);
			}
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void receiveHandshakePacket(Packet1Handshake packet) {
		boolean result = false;
		if(networkSide == NetworkSide.CLIENT) {
			result = clientListener.onHandshake(packet.getProtocolName(), packet.getProtocolVersion());
		} else {
			result = serverListener.onHandshake(socket.getPort(), packet.getProtocolName(), packet.getProtocolVersion());
		}
		if(!result) {
			KudesuNetwork.log(Level.ERROR, "Handshake failed!");
			requestDropConnection();
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void receiveAuthorizationPacket(Packet2Authorization packet) {
		boolean result = false;
		if(networkSide == NetworkSide.CLIENT) {
			result = clientListener.onAuthorization(packet.getData());
		} else {
			result = serverListener.onAuthorization(socket.getPort(), packet.getData());
		}
		if(!result) {
			KudesuNetwork.log(Level.ERROR, "Authorization failed!");
			requestDropConnection();
		} else {
			isNetworkReady = true;
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void receivePingPacket(Packet3Ping packet) {
		boolean result = false;
		if(networkSide == NetworkSide.CLIENT) {
			result = clientListener.onPing(packet.getPingId(), packet.getTimestampSended(), packet.getTimestampReceived());
		} else {
			result = serverListener.onPing(socket.getPort(), packet.getPingId(), packet.getTimestampSended(), packet.getTimestampReceived());
		}
		if(!result) {
			KudesuNetwork.log(Level.ERROR, "Ping check failed!");
			requestDropConnection();
		}
	}
	
	public void requestDropConnection() {
		requestDropConnection(false, NetworkBase.SEVERE_DISCONNECTION);
	}
	
	private void receiveDisconnectionPacket(Packet5Disconnect packet) {
		requestDropConnection(false, packet.getReason());
	}
	
	@ThreadSafe(callerThread = "Unknown")
	public void sendPacket(Packet packet) {
		networkWorker.givePacketToSend(packet);
	}
	
	@ThreadSafe(callerThread = "Unknown")
	public void requestDropConnection(boolean byAction, int reason) {
		if(!isDropConnectionCalled) {
			KudesuNetwork.log(Level.INFO, "Closing connection...");
			isNetworkReady = false;
			isDropConnectionCalled = true;
			if(byAction) {
				sendPacket(new Packet5Disconnect(reason));
				while(!isDisconnectPacketSended) { //Wait for a packet sent by a normal pipeline to disconnect
					try {
						if(!Thread.currentThread().getName().contains(WORKER_THREAD_NAME)) {
							Thread.sleep(10L); //Sleep while waiting packet to be sended, but not for worker thread!
						}
					} catch(InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			TaskManager.executeOnce(() -> dropConnectionInternal(reason), "Drop Connection", THREAD_TERMINATION_TIME);
		}
	}
	
	/**
	 * Must be called only from requestDropConnection method
	 */
	@ThreadSafe(callerThread = "Drop Connection")
	private void dropConnectionInternal(int reason) {
		if(isAlive()) {
			try {
				socket.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		stopWorkerThreads();
		base.onConnectionDropped(socket.getPort());
		KudesuNetwork.log(Level.INFO, "Connection closed: " + socket.getInetAddress() + ":" + socket.getPort() + " / " + socket.getLocalPort());
		if(networkSide == NetworkSide.CLIENT) {
			clientListener.onDisconnection(reason);
		} else {
			serverListener.onDisconnection(socket.getLocalPort(), reason);
		}
	}
	
	@ThreadSafe(callerThread = "Drop Connection")
	private void stopWorkerThreads() {
		KudesuNetwork.log(Level.DEBUG, "Stop and shutdown network reader...");
		networkReader.stop();
		shutdownThread(readerExecutorService, "Network reader");
		KudesuNetwork.log(Level.DEBUG, "Stop and shutdown network worker...");
		networkWorker.stop();
		shutdownThread(workerExecutorService, "Network worker");
	}
	
	@ThreadSafe(callerThread = "Drop Connection")
	private void shutdownThread(ExecutorService executorService, String threadName) {
		executorService.shutdown();
		awaitTermination(executorService, threadName);
	}
	
	@ThreadSafe(callerThread = "Drop Connection")
	private void awaitTermination(ExecutorService executorService, String threadName) {
		try {
			if(executorService.awaitTermination(THREAD_TERMINATION_TIME, TimeUnit.MILLISECONDS)) {
				KudesuNetwork.log(Level.DEBUG, threadName + " terminated!");
			} else {
				KudesuNetwork.log(Level.FATAL, threadName + " not terminated!");
			}
		} catch(InterruptedException ex) {
			ex.printStackTrace();
			//Cause fatal problem (many waiting threads), make soft exit
		}
	}
	
	public boolean isAlive() {
		return socket.isConnected() && !socket.isClosed();
	}
	
	public boolean isPacketExist(int packetId) {
		return base.isPacketExist(packetId);
	}
	
	public boolean isProtocolPacket(int packetId) {
		return base.isProtocolPacket(packetId);
	}
	
	public Packet getPacketContainer(int packetId) {
		return base.getPacketContainer(packetId);
	}
	
	public boolean isNetworkReady() {
		return isNetworkReady;
	}
	
	public void setDisconnectSended() {
		isDisconnectPacketSended = true;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public boolean useProtocol() {
		return useProtocol;
	}
	
	public NetworkParameters getParameters() {
		return parameters;
	}
	
	public NetworkSide getNetworkSide() {
		return networkSide;
	}
}

package ru.kudesunik.kudesunetwork.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.crypto.spec.IvParameterSpec;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.KudesuNetworkFlags;
import ru.kudesunik.kudesunetwork.annotations.NonNull;
import ru.kudesunik.kudesunetwork.annotations.ThreadSafe;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet3Ping;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.packet.Packet5Disconnect;
import ru.kudesunik.kudesunetwork.parameters.PingParameters;
import ru.kudesunik.kudesunetwork.util.NetworkCipher;
import ru.kudesunik.kudesunetwork.util.Utilities;
import ru.kudesunik.kudesunetwork.util.task.TaskManager;
import ru.kudesunik.kudesunetwork.util.task.TaskManagerTask;

public class NetworkWorker implements Runnable {
	
	private final NetworkHandler handler;
	private final OutputStream outputStream;
	
	private final DataOutputStream outputStreamData;
	
	private final ConcurrentLinkedDeque<Packet> recievedPackets;
	private final ConcurrentLinkedDeque<Packet> packetsToSend;
	
	private final PingParameters pingParameters;
	
	private volatile boolean isWorking;
	
	public NetworkWorker(NetworkHandler handler, boolean useProtocol) {
		this.handler = handler;
		this.pingParameters = handler.getParameters().getPingParameters();
		this.outputStream = handler.getOutputStream();
		this.outputStreamData = new DataOutputStream(outputStream);
		this.recievedPackets = new ConcurrentLinkedDeque<>();
		this.packetsToSend = new ConcurrentLinkedDeque<>();
		this.isWorking = true;
	}
	
	/**
	 * Put received packet to worker queue for further sending it to listener
	 * @param packet - recieved packet
	 */
	@ThreadSafe(callerThread = "KudesuNetwork Reader")
	public void receivePacket(Packet packet) {
		if(packet.getId() == Packet3Ping.ID) {
			Packet3Ping pingPacket = (Packet3Ping) packet;
			if(pingPacket.getNetworkSide() == handler.getNetworkSide()) {
				pingPacket.setTimestampReceived(); //Set real receive timestamp and handle it
			} else {
				givePacketToSend(pingPacket); //Send packet back safely, don't handle
				return;
			}
		}
		synchronized(recievedPackets) {
			this.recievedPackets.addLast(packet);
		}
		synchronized(this) {
			notifyAll();
		}
	}
	
	/**
	 * Send recieved packet to listener
	 */
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void sendReceivedPacket() {
		Packet packet;
		synchronized(recievedPackets) {
			packet = recievedPackets.poll();
		}
		if(packet != null) {
			handler.receivePacket(packet);
		}
	}
	
	/**
	 * Put packet for send to worker queue for further sending it to server or client
	 * @param packet - packet to send
	 */
	@ThreadSafe(callerThread = "Unknown")
	public void givePacketToSend(Packet packet) {
		if(handler.isNetworkReady() || handler.isProtocolPacket(packet.getId())) {
			synchronized(packetsToSend) {
				packetsToSend.addLast(packet);
			}
			synchronized(this) {
				notifyAll();
			}
		} else {
			KudesuNetwork.log(Level.ERROR, "Network is not ready for packet sending! Packet id: " + packet.getId());
		}
	}
	
	/**
	 * Send packet in queue to server or client
	 */
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void sendPacket() {
		Packet packet;
		synchronized(packetsToSend) {
			packet = packetsToSend.poll();
		}
		if(packet != null) {
			sendPacket(packet);
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void sendPacket(@NonNull Packet packet) {
		byte packetId = packet.getId();
		if(handler.isPacketExist(packetId)) {
			if(packet.getId() == Packet4Raw.ID) {
				sendRawPacket(packet);
				return;
			}
			if(packet.getId() == Packet3Ping.ID) {
				Packet3Ping pingPacket = (Packet3Ping) packet;
				pingPacket.setTimestampSended();
			}
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			DataOutputStream packetData = new DataOutputStream(data);
			try {
				packet.write(packetData);
				if(packetData.size() > KudesuNetwork.MAX_PACKET_SIZE) {
					KudesuNetwork.log(Level.ERROR, "Huge packet (" + packetData.size() + " bytes), send skipping, please split data to few packets!");
					return;
				}
				outputStreamData.writeByte(packetId); //Write packet id (1 byte)
				int packetFlag = 0;
				if(handler.getParameters().isEncrypt()) {
					packetFlag = KudesuNetworkFlags.appendFlag(packetFlag, KudesuNetworkFlags.ENCRYPTED);
				}
				if(handler.getParameters().isCompress()) {
					packetFlag = KudesuNetworkFlags.appendFlag(packetFlag, KudesuNetworkFlags.COMPRESSED);
				}
				outputStreamData.writeByte(packetFlag); //Write packet flag (1 byte)
				byte[] payloadData = data.toByteArray();
				if(KudesuNetworkFlags.checkFlag(packetFlag, KudesuNetworkFlags.ENCRYPTED)) {
					IvParameterSpec iv = NetworkCipher.generateIV();
					outputStreamData.write(iv.getIV()); //Write encryption initialization vector (16 bytes)
					payloadData = handler.getCipher().encrypt(payloadData, iv);
				}
				if(KudesuNetworkFlags.checkFlag(packetFlag, KudesuNetworkFlags.COMPRESSED)) {
					payloadData = Utilities.compress(payloadData);
				}
				outputStreamData.writeInt(payloadData.length); //Write payload data length
				outputStreamData.write(payloadData); //Write payload data
				data.close();
				packetData.close();
			} catch(IOException ex) {
				handler.requestDropConnection();
			}
			if(packet.getId() == Packet5Disconnect.ID) {
				handler.setDisconnectSended();
			}
		} else {
			KudesuNetwork.log(Level.ERROR, "Packet not found!");
		}
	}
	
	@ThreadSafe(callerThread = "KudesuNetwork Worker")
	private void sendRawPacket(Packet packet) {
		try {
			packet.write(outputStreamData);
		} catch(IOException ex) {
			handler.requestDropConnection();
		}
	}
	
	public boolean isPacketExist(int packetId) {
		return handler.isPacketExist(packetId);
	}
	
	public Packet getPacketContainer(int packetId) {
		return handler.getPacketContainer(packetId);
	}
	
	@Override
	public void run() {
		boolean repeatFlag = true;
		if(pingParameters.isEnabled() && handler.useProtocol()) {
			TaskManagerTask task = new TaskManagerTask("Ping task", () -> {
				Packet3Ping pingPacket = new Packet3Ping();
				pingPacket.setSide(handler.getNetworkSide());
				givePacketToSend(pingPacket);
			});
			task.setUpdateTime(pingParameters.getDelay());
			TaskManager.execute(task);
		}
		while(isWorking) {
			synchronized(recievedPackets) {
				while(!recievedPackets.isEmpty()) {
					sendReceivedPacket();
					repeatFlag = true;
				}
			}
			synchronized(packetsToSend) {
				while(!packetsToSend.isEmpty()) {
					sendPacket();
					repeatFlag = true;
				}
			}
			if(repeatFlag) {
				repeatFlag = false;
			} else {
				try {
					synchronized(this) {
						wait();
					}
					repeatFlag = true;
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		KudesuNetwork.log(Level.DEBUG, "Network worker stopped!");
	}
	
	/**
	 * Network reader stop command
	 */
	public void stop() {
		isWorking = false;
		synchronized(this) {
			notifyAll();
		}
	}
}

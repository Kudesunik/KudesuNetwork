package ru.kudesunik.kudesunetwork.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.annotations.ThreadSafe;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;

public class NetworkWorker implements Runnable {
	
	private final NetworkHandler handler;
	private final OutputStream outputStream;
	
	private final ConcurrentLinkedDeque<Packet> recievedPackets;
	private final ConcurrentLinkedDeque<Packet> packetsToSend;
	
	private volatile boolean isWorking;
	
	public NetworkWorker(NetworkHandler handler, boolean useProtocol) {
		this.handler = handler;
		this.outputStream = handler.getOutputStream();
		this.recievedPackets = new ConcurrentLinkedDeque<>();
		this.packetsToSend = new ConcurrentLinkedDeque<>();
		this.isWorking = true;
	}
	
	/**
	 * Put received packet to worker queue for further sending it to listener
	 * @param packet - recieved packet
	 */
	@ThreadSafe(callerThread = "KudeSocket Reader")
	public void receivePacket(Packet packet) {
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
	@ThreadSafe(callerThread = "KudeSocket Worker")
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
		}
	}
	
	/**
	 * Send packet in queue to server or client
	 */
	@ThreadSafe(callerThread = "KudeSocket Worker")
	private void sendPacket() {
		Packet packet;
		synchronized(packetsToSend) {
			packet = packetsToSend.poll();
		}
		if(packet != null) {
			if(packet.getId() == Packet4Raw.ID) {
				sendRawPacket(packet);
				return;
			}
			byte packetId = packet.getId();
			if(handler.isPacketExist(packetId)) {
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				DataOutputStream packetData = new DataOutputStream(data);
				DataOutputStream overallData = new DataOutputStream(outputStream);
				try {
					packet.write(packetData);
					overallData.writeByte(packetId);
					overallData.writeShort(packetData.size());
					overallData.write(data.toByteArray());
				} catch(IOException ex) {
					handler.requestDropConnection();
				}
			} else {
				KudesuNetwork.log(Level.ERROR, "Packet not found!");
			}
		}
	}
	
	@ThreadSafe(callerThread = "KudeSocket Worker")
	private void sendRawPacket(Packet packet) {
		try {
			packet.write(new DataOutputStream(outputStream));
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
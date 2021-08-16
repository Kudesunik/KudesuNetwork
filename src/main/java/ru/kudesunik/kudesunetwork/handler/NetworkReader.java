package ru.kudesunik.kudesunetwork.handler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.util.Combiner;

public class NetworkReader implements Runnable {
	
	private final NetworkHandler handler;
	private final InputStream inputStream;
	private final NetworkWorker socketWorker;
	private final boolean useProtocol;
	
	private volatile boolean isWorking;
	
	public NetworkReader(NetworkHandler handler, NetworkWorker socketWorker, boolean useProtocol) {
		this.handler = handler;
		this.inputStream = handler.getInputStream();
		this.socketWorker = socketWorker;
		this.useProtocol = useProtocol;
		this.isWorking = true;
	}
	
	@Override
	public void run() {
		while(isWorking) {
			if(useProtocol) {
				readProtocolPacket();
			} else {
				readRawPacket();
			}
		}
	}
	
	private void readProtocolPacket() {
		int packetId = 0;
		try {
			packetId = inputStream.read(); //Blocking input stream read first byte
		} catch(IOException ex) {
			handler.requestDropConnection();
			return;
		}
		if((packetId > 0) && socketWorker.isPacketExist(packetId)) {
			short packetSize;
			try {
				packetSize = Combiner.combineBytes((byte) inputStream.read(), (byte) inputStream.read());
			} catch(IOException ex) {
				KudesuNetwork.log(Level.ERROR, "Error on reading unknown packet");
				handler.requestDropConnection();
				return;
			}
			Packet packet = socketWorker.getPacketContainer(packetId);
			try {
				packet.read(new DataInputStream(new ByteArrayInputStream(inputStream.readNBytes(packetSize))));
			} catch(IOException ex) {
				KudesuNetwork.log(Level.ERROR, "Error on reading packet id " + packetId + " with declared size of " + packetSize + " bytes");
				handler.requestDropConnection();
				return;
			}
			if(isWorking) {
				socketWorker.receivePacket(packet);
			}
		} else if(packetId == (-1)) { //EOF
			handler.requestDropConnection();
		} else if(packetId != 0) {
			KudesuNetwork.log(Level.ERROR, "Packet with id " + packetId + " not found!");
		}
	}
	
	private void readRawPacket() {
		byte firstByte = 0;
		int availableBytes = 0;
		try {
			firstByte = (byte) inputStream.read(); //Blocking input stream read first byte
			availableBytes = inputStream.available();
		} catch(IOException ex) {
			handler.requestDropConnection();
			return;
		}
		if(firstByte == (-1)) { //EOF
			handler.requestDropConnection();
			return;
		}
		Packet4Raw packet = new Packet4Raw(firstByte, new byte[availableBytes + 1]);
		try {
			packet.read(new DataInputStream(new ByteArrayInputStream(inputStream.readNBytes(availableBytes))));
		} catch(IOException ex) {
			KudesuNetwork.log(Level.ERROR, "Error on reading raw packet with declared size of " + (availableBytes + 1) + " bytes");
			handler.requestDropConnection();
			return;
		}
		if(isWorking) {
			socketWorker.receivePacket(packet);
		}
	}
	
	public void stop() {
		isWorking = false;
	}
}

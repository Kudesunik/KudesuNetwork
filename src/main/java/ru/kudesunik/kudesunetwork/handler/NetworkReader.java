package ru.kudesunik.kudesunetwork.handler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import javax.crypto.spec.IvParameterSpec;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.KudesuNetworkFlags;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.packet.PacketProgress;
import ru.kudesunik.kudesunetwork.util.Combiner;
import ru.kudesunik.kudesunetwork.util.Utilities;

public class NetworkReader implements Runnable {
	
	private final NetworkHandler handler;
	private final InputStream inputStream;
	private final NetworkWorker networkWorker;
	private final Consumer<PacketProgress> progress;
	private final boolean useProtocol;
	
	private volatile boolean isWorking;
	
	public NetworkReader(NetworkHandler handler, NetworkWorker networkWorker, Consumer<PacketProgress> progress, boolean useProtocol) {
		this.handler = handler;
		this.inputStream = handler.getInputStream();
		this.networkWorker = networkWorker;
		this.progress = progress;
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
	
	public static int getPacketSize(InputStream inputStream) throws IOException {
		short s1 = Combiner.combineBytes((byte) inputStream.read(), (byte) inputStream.read());
		short s2 = Combiner.combineBytes((byte) inputStream.read(), (byte) inputStream.read());
		return Combiner.combineShorts(s1, s2);
	}
	
	private void readProtocolPacket() {
		int packetId = 0;
		try {
			packetId = inputStream.read(); //Blocking input stream read first byte (packet id)
		} catch(IOException ex) {
			handler.requestDropConnection();
			return;
		}
		if((packetId > 0) && networkWorker.isPacketExist(packetId)) {
			int packetFlag = 0;
			try {
				packetFlag = inputStream.read(); //Blocking input stream read second byte (packet flag)
			} catch(IOException ex) {
				handler.requestDropConnection();
				return;
			}
			byte[] iv = null;
			if(KudesuNetworkFlags.checkFlag(packetFlag, KudesuNetworkFlags.ENCRYPTED)) {
				iv = new byte[16];
				try {
					Utilities.readBytes(inputStream, iv, 0, iv.length);
				} catch(IOException e) {
					handler.requestDropConnection();
					return;
				}
			}
			int payloadSize;
			try {
				payloadSize = getPacketSize(inputStream);
			} catch(IOException ex) {
				KudesuNetwork.log(Level.ERROR, "Error on reading unknown packet");
				handler.requestDropConnection();
				return;
			}
			Packet packet = networkWorker.getPacketContainer(packetId);
			try {
				byte[] payload = new byte[payloadSize];
				int readCount = payloadSize / KudesuNetwork.MAX_DATA_SIZE;
				for(int i = 0; i < readCount; i++) {
					int currentReadCount = i * KudesuNetwork.MAX_DATA_SIZE;
					Utilities.readBytes(inputStream, payload, currentReadCount, KudesuNetwork.MAX_DATA_SIZE);
					if(!KudesuNetwork.isProtocolPacket(packet.getId())) {
						progress.accept(new PacketProgress(packetId, payloadSize, currentReadCount));
					}
				}
				int totalReadCount = readCount * KudesuNetwork.MAX_DATA_SIZE;
				Utilities.readBytes(inputStream, payload, totalReadCount, (payloadSize - totalReadCount));
				if(KudesuNetworkFlags.checkFlag(packetFlag, KudesuNetworkFlags.COMPRESSED)) {
					payload = Utilities.decompress(payload);
				}
				if(iv != null) {
					payload = handler.getCipher().decrypt(payload, new IvParameterSpec(iv));
				}
				packet.read(new DataInputStream(new ByteArrayInputStream(payload)));
				if(!KudesuNetwork.isProtocolPacket(packet.getId())) {
					progress.accept(new PacketProgress(packetId, payloadSize, payloadSize));
				}
			} catch(IOException ex) {
				ex.printStackTrace();
				KudesuNetwork.log(Level.ERROR, "Error on reading packet id " + packetId + " with declared size of " + payloadSize + " bytes");
				handler.requestDropConnection();
				return;
			}
			if(isWorking) {
				networkWorker.receivePacket(packet);
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
			byte[] array = new byte[availableBytes];
			Utilities.readBytes(inputStream, array, 0, availableBytes);
			packet.read(new DataInputStream(new ByteArrayInputStream(array)));
		} catch(IOException ex) {
			KudesuNetwork.log(Level.ERROR, "Error on reading raw packet with declared size of " + (availableBytes + 1) + " bytes");
			handler.requestDropConnection();
			return;
		}
		if(isWorking) {
			networkWorker.receivePacket(packet);
		}
	}
	
	public void stop() {
		isWorking = false;
	}
}

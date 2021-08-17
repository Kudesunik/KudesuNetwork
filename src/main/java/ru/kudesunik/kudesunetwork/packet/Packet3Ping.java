package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.handler.NetworkSide;
import ru.kudesunik.kudesunetwork.util.RandomMath;

public class Packet3Ping implements Packet {
	
	public static final int ID = 3;
	
	private long id;
	
	private byte side;
	
	private long timestampSended;
	private long timestampReceived;
	
	public Packet3Ping() {
		this.id = RandomMath.nextLong();
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeLong(id);
		data.writeByte(side);
		data.writeLong(timestampSended);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		id = data.readLong();
		side = data.readByte();
		timestampSended = data.readLong();
	}
	
	public long getPingId() {
		return id;
	}
	
	public NetworkSide getNetworkSide() {
		return NetworkSide.getById(side);
	}
	
	public void setSide(NetworkSide side) {
		this.side = side.getId();
	}
	
	public long getTimestampSended() {
		return timestampSended;
	}
	
	public void setTimestampSended() {
		this.timestampSended = System.nanoTime();
	}
	
	public long getTimestampReceived() {
		return timestampReceived;
	}
	
	public void setTimestampReceived() {
		this.timestampReceived = System.nanoTime();
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

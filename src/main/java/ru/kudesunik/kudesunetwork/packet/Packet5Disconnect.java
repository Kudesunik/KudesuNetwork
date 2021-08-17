package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet5Disconnect implements Packet {
	
	public static final int ID = 5;
	
	private int reason;
	
	public Packet5Disconnect(int reason) {
		this.reason = reason;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(reason);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		reason = data.readInt();
	}
	
	public int getReason() {
		return reason;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

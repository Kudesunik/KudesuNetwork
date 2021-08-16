package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet4Raw implements Packet {
	
	public static final int ID = 4;
	
	private byte[] data;
	
	public Packet4Raw(byte firstByte, byte[] data) {
		this.data = data;
		this.data[0] = firstByte;
	}
	
	public Packet4Raw(byte[] data) {
		this.data = data;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.write(this.data);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		byte[] dataArray = data.readAllBytes();
		System.arraycopy(dataArray, 0, this.data, 1, dataArray.length);
	}
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

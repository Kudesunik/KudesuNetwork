package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2Authorization implements Packet {
	
	public static final int ID = 2;
	
	private String data;
	
	public Packet2Authorization(String data) {
		this.data = data;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(this.data);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		this.data = data.readUTF();
	}
	
	public String getData() {
		return data;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

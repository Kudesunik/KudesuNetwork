package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.KudesuNetwork;

public class Packet1Handshake implements Packet {
	
	public static final int ID = 1;
	
	private String protocolName;
	private short protocolVersion;
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(KudesuNetwork.PROTOCOL_NAME);
		data.writeShort(KudesuNetwork.PROTOCOL_VERSION);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		this.protocolName = data.readUTF();
		this.protocolVersion = data.readShort();
	}
	
	public String getProtocolName() {
		return protocolName;
	}
	
	public short getProtocolVersion() {
		return protocolVersion;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

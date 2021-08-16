package ru.kudesunik.kudesocket.examples.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class Packet13Image implements Packet {
	
	public static final int ID = 13;
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

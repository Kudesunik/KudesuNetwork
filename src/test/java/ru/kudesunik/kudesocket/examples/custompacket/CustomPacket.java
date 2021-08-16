package ru.kudesunik.kudesocket.examples.custompacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class CustomPacket implements Packet {
	
	public static final int ID = 10;
	
	private int customNumber1;
	private int customNumber2;
	
	public CustomPacket() {
		this.customNumber1 = 0;
		this.customNumber2 = 0;
	}
	
	public int getCustomNumber1() {
		return customNumber1;
	}
	
	public void setCustomNumber1(int number) {
		this.customNumber1 = number;
	}
	
	public int getCustomNumber2() {
		return customNumber2;
	}
	
	public void setCustomNumber2(int number) {
		this.customNumber2 = number;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(customNumber1);
		data.writeInt(customNumber2);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		customNumber1 = data.readInt();
		customNumber2 = data.readInt();
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

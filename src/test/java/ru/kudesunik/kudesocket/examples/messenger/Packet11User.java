package ru.kudesunik.kudesocket.examples.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class Packet11User implements Packet {
	
	public static final int ID = 11;
	
	private boolean isConnected;
	
	private String login;
	
	public Packet11User(boolean isConnected, String login) {
		this.isConnected = isConnected;
		this.login = login;
	}
	
	public Packet11User() {
		//Registrator constructor
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeBoolean(isConnected);
		data.writeUTF(login);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		isConnected = data.readBoolean();
		login = data.readUTF();
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public String getLogin() {
		return login;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

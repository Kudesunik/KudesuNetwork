package ru.kudesunik.kudesocket.examples.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class Packet10Login implements Packet {
	
	public static final int ID = 10;
	
	private String login;
	private String password;
	
	public Packet10Login(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public Packet10Login() {
		//Registrator constructor
	}

	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(login);
		data.writeUTF(password);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		login = data.readUTF();
		password = data.readUTF();
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

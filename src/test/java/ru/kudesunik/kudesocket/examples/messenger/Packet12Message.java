package ru.kudesunik.kudesocket.examples.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class Packet12Message implements Packet {
	
	public static final int ID = 12;
	
	private Message message;
	
	public Packet12Message(Message message) {
		this.message = message;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		String login = message.getLogin();
		if(login != null) {
			data.writeUTF(message.getLogin());
		} else {
			data.writeUTF("");
		}
		data.writeUTF(message.getText());
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		message = new Message(data.readUTF(), data.readUTF());
	}
	
	public Message getMessage() {
		return message;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

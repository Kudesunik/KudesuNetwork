package ru.kudesunik.kudesocket.examples.messenger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MessengerListener implements ActionListener {
	
	private final MessengerGUI gui;
	private final MessengerHandler handler;
	
	public MessengerListener(MessengerGUI gui, MessengerHandler handler) {
		this.gui = gui;
		this.handler = handler;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if(source instanceof JButton) {
			JButton button = (JButton) source;
			switch(button.getName()) {
			case "Create server":
				startServer();
				break;
			case "Connect":
				if(handler.isClientConnected()) {
					disconnect();
				} else {
					connect();
				}
				break;
			default:
				break;
			}
		}
	}
	
	private void startServer() {
		int port = gui.getPort();
		if(port != 0) {
			gui.setInformation("Starting server...");
			if(handler.startServer(port)) {
				gui.setInformation("Server started!");
				gui.setServerStatus("Working");
			}
		} else {
			gui.setInformation("Server port not valid!");
		}
	}
	
	private void connect() {
		String address = gui.getAddress();
		int port = gui.getPort();
		String login = gui.getLogin();
		String password = gui.getPassword();
		if((address != null) && !address.isEmpty() && (port != 0)) {
			if((login != null) && !login.isEmpty() && (password != null) && !password.isEmpty()) {
				gui.setInformation("Connecting to client...");
				if(handler.connectToServer(address, port, login, password)) {
					gui.setConnection(true);
					gui.setInformation("Client connected to server!");
					gui.setClientStatus("Connected");
				} else {
					gui.setInformation("Connection failed!");
				}
			} else {
				gui.setInformation("Credentials not valid");
			}
		} else {
			gui.setInformation("Connection address or port not valid");
		}
	}
	
	private void disconnect() {
		handler.disconnectFromServer();
		gui.setInformation("Client disconnected from server!");
		gui.setClientStatus("Disconnected");
		gui.setConnection(false);
	}
}

package ru.kudesunik.kudesocket.examples.messenger;

public class Messenger {
	
	private final MessengerGUI gui;
	private final MessengerHandler handler;
	
	public Messenger() {
		this.handler = new MessengerHandler();
		this.gui = new MessengerGUI(handler);
	}
	
	private void start() {
		gui.setVisible(true);
	}
	
	public static void main(String[] args) {
		(new Messenger()).start();
	}
}

package ru.kudesunik.kudesunetwork.examples.messenger;

public class Messenger {
	
	private final MessengerGUI gui;
	private final MessengerListener listener;
	private final MessengerHandler handler;
	
	public Messenger() {
		this.listener = new MessengerListener();
		this.gui = new MessengerGUI(listener);
		this.handler = new MessengerHandler(gui);
		this.listener.attachHandler(handler);
	}
	
	private void start() {
		gui.setVisible(true);
	}
	
	public static void main(String[] args) {
		(new Messenger()).start();
	}
}

package ru.kudesunik.kudesunetwork.examples.ping;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;

public class PingPacketClient {
	
	private final PingClientNetworkListener networkListener;
	private final NetworkParameters parameters;
	
	public PingPacketClient() {
		this.networkListener = new PingClientNetworkListener();
		this.parameters = new NetworkParameters();
		this.parameters.setAuthorization("Login", "Password");
	}
	
	private void start() {
		KudesuNetworkClient client = KudesuNetwork.createClient("127.0.0.1", 8888, null, networkListener, parameters);
		client.connect();
		while(client.isAlive()) {
			try {
				Thread.sleep(2000L);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		(new PingPacketClient()).start();
	}
}

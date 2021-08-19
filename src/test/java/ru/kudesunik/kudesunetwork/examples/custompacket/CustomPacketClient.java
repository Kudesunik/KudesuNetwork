package ru.kudesunik.kudesunetwork.examples.custompacket;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;

public class CustomPacketClient {
	
	private final CustomPacketRegistrator registrator;
	private final CustomClientNetworkListener networkListener;
	private final NetworkParameters parameters;
	
	public CustomPacketClient() {
		this.registrator = new CustomPacketRegistrator();
		this.networkListener = new CustomClientNetworkListener();
		this.parameters = new NetworkParameters();
		this.parameters.setAuthorization("Login", "Password");
	}
	
	private void start() {
		KudesuNetworkClient client = KudesuNetwork.createClient("127.0.0.1", 8888, registrator, networkListener, parameters);
		client.connect();
		while(client.isAlive()) {
			CustomPacket packet = new CustomPacket();
			packet.setCustomNumber1(100);
			packet.setCustomNumber2(200);
			client.sendPacket(packet);
			try {
				Thread.sleep(2000L);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		(new CustomPacketClient()).start();
	}
}

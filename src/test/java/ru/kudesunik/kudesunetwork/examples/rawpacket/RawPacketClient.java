package ru.kudesunik.kudesunetwork.examples.rawpacket;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.packet.Packet4Raw;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;

public class RawPacketClient {
	
	private final RawClientNetworkListener networkListener;
	private final NetworkParameters parameters;
	
	public RawPacketClient() {
		this.networkListener = new RawClientNetworkListener();
		this.parameters = new NetworkParameters();
	}
	
	private void start() {
		KudesuNetworkClient client = KudesuNetwork.createClient("localhost", 8888, null, networkListener, parameters, false);
		client.connect();
		while(client.isAlive()) {
			Packet4Raw packet = new Packet4Raw(new byte[]{0, 1, 2, 3, 4, 5});
			client.sendPacket(packet);
			try {
				Thread.sleep(1000L);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		(new RawPacketClient()).start();
	}
}

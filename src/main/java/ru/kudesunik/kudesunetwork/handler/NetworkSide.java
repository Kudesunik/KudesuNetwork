package ru.kudesunik.kudesunetwork.handler;

public enum NetworkSide {
	
	CLIENT((byte) 0), SERVER((byte) 1);
	
	private static final NetworkSide[] values = values();
	private static final NetworkSide[] byId = new NetworkSide[values.length];
	
	private final byte id;
	
	private NetworkSide(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return id;
	}
	
	public static NetworkSide getById(int id) {
		return byId[id];
	}
	
	static {
		for(NetworkSide ps : values) {
			byId[ps.id] = ps;
		}
	}
}
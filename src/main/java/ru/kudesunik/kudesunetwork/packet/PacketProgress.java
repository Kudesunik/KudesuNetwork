package ru.kudesunik.kudesunetwork.packet;

public class PacketProgress {
	
	private final int packetId;
	private final int totalSize;
	private final int currentSize;
	
	public PacketProgress(int packetId, int totalSize, int currentSize) {
		this.packetId = packetId;
		this.totalSize = totalSize;
		this.currentSize = currentSize;
	}
	
	public int getPacketId() {
		return packetId;
	}
	
	public int getTotalSize() {
		return totalSize;
	}
	
	public int getCurrentSize() {
		return currentSize;
	}
}

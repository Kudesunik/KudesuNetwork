package ru.kudesunik.kudesunetwork.parameters;

public class PingParameters {
	
	private final boolean isEnabled;
	private final int delay;
	
	public PingParameters(boolean isEnabled, int delay) {
		this.isEnabled = isEnabled;
		this.delay = delay;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Ping parameters with enabled ping (every 1000ms)
	 */
	public static PingParameters defaultPing() {
		return new PingParameters(true, 1000);
	}
	
	/**
	 * No ping
	 */
	public static PingParameters noPing() {
		return new PingParameters(false, 0);
	}
}

package ru.kudesunik.kudesunetwork.parameters;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import ru.kudesunik.kudesunetwork.annotations.Nullable;

public class NetworkParameters {
	
	private boolean sendHandshake;
	private PingParameters pingParameters;
	
	private String authorizationData;
	
	public NetworkParameters() {
		this.sendHandshake = true;
		this.authorizationData = null;
		this.pingParameters = PingParameters.defaultPing();
	}
	
	public @Nullable String getAuthorization() {
		return authorizationData;
	}
	
	public void setAuthorization(String login, String password) {
		authorizationData = Base64.getEncoder().encodeToString((login + ":" + password).getBytes(StandardCharsets.UTF_8));
	}
	
	public boolean getSendHandshake() {
		return sendHandshake;
	}
	
	public void setSendHandshake(boolean send) {
		this.sendHandshake = send;
	}
	
	public PingParameters getPingParameters() {
		return pingParameters;
	}
	
	public void setPingParameters(PingParameters pingParameters) {
		this.pingParameters = pingParameters;
	}
}

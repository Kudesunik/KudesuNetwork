package ru.kudesunik.kudesunetwork.examples.messenger;

import java.awt.image.BufferedImage;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.annotations.ClientSide;
import ru.kudesunik.kudesunetwork.annotations.ServerSide;
import ru.kudesunik.kudesunetwork.client.KudesuNetworkClient;
import ru.kudesunik.kudesunetwork.client.NetworkClientListener;
import ru.kudesunik.kudesunetwork.packet.Packet;
import ru.kudesunik.kudesunetwork.packet.PacketRegistrator;
import ru.kudesunik.kudesunetwork.parameters.NetworkParameters;
import ru.kudesunik.kudesunetwork.server.KudesuNetworkServer;
import ru.kudesunik.kudesunetwork.server.NetworkServerListener;

public class MessengerHandler {
	
	public static final int DISCONNECT_USER = 2;
	public static final int DISCONNECT_INVALID_PASSWORD = 3;
	public static final int DISCONNECT_LOGIN_EXISTS = 4;
	
	private final MessengerGUI gui;
	
	private final PacketRegistrator registrator;
	private final NetworkServerListener serverListener;
	private final NetworkClientListener clientListener;
	private final NetworkParameters parameters;
	
	private KudesuNetworkServer server;
	private KudesuNetworkClient client;
	
	private final Int2ObjectMap<String> users;
	private final Int2ObjectMap<Object> data;
	
	private String serverPassword;
	
	public MessengerHandler(MessengerGUI gui) {
		this.gui = gui;
		this.registrator = new MessengerPacketRegistrator();
		this.serverListener = new MessengerServerNetworkListener(this);
		this.clientListener = new MessengerClientNetworkListener(this);
		this.parameters = new NetworkParameters();
		this.users = new Int2ObjectArrayMap<>();
		this.data = new Int2ObjectArrayMap<>();
		this.serverPassword = "";
		setupParameters();
	}
	
	private void setupParameters() {
		parameters.setSendHandshake(true);
	}
	
	@ClientSide
	public boolean connectToServer(String address, int port, String login, String password) {
		if(!isClientConnected()) {
			client = KudesuNetwork.createClient(address, port, registrator, clientListener, parameters);
			if(client.connect()) {
				client.sendPacket(new Packet10Login(login, password));
				return true;
			}
		}
		return false;
	}
	
	@ClientSide
	public void disconnectFromServer() {
		if(isClientConnected()) {
			client.disconnect(DISCONNECT_USER);
		}
	}
	
	public void startServer() {
		int port = gui.getPort();
		if(port != 0) {
			gui.setInformation("Starting server...");
			if(startServer(port)) {
				gui.setInformation("Server started!");
				gui.setServerStatus("Working");
				gui.setServerWorking(true);
			}
		} else {
			gui.setInformation("Server port not valid!");
		}
	}
	
	public boolean startServer(int port) {
		if(!isServerWorking()) {
			server = KudesuNetwork.createServer(port, registrator, serverListener, parameters);
			this.serverPassword = gui.getPassword();
			return server.start();
		} else {
			return false;
		}
	}
	
	public void stopServer() {
		if(isServerWorking()) {
			server.stop();
			gui.setInformation("Server stopped!");
			gui.setServerStatus("Stopped");
			gui.setServerWorking(false);
		}
	}
	
	@ServerSide
	public void handleLogin(int port, String login, String password) {
		if(!users.containsKey(port)) {
			if(serverPassword.equals(password)) {
				if(!users.containsValue(login)) {
					users.put(port, login);
					server.sendBroadcast(new Packet11User(true, login));
					sendAllConnectedUsers(port);
					sendAllMessages(port);
				} else {
					server.disconnectClient(port, DISCONNECT_LOGIN_EXISTS);
				}
			} else {
				server.disconnectClient(port, DISCONNECT_INVALID_PASSWORD);
			}
		}
	}
	
	@ServerSide
	public void handleMessage(int port, String text) {
		if(users.containsKey(port)) {
			String login = users.get(port);
			Message message = new Message(login, text);
			data.put(data.size(), message);
			server.sendBroadcast(new Packet12Message(message));
		}
	}
	
	@ServerSide
	public void handleImage(int port, Packet13Image imagePacket) {
		data.put(data.size(), imagePacket);
		server.sendBroadcast(imagePacket);
	}
	
	@ClientSide
	public void handleImage(BufferedImage image) {
		gui.writeImageMessage(image);
	}
	
	@ClientSide
	public void handleMessage(String login, String text) {
		gui.writeUserMessage(login, text);
	}
	
	@ServerSide
	private void sendAllConnectedUsers(int port) {
		for(Entry<String> entry : users.int2ObjectEntrySet()) {
			if(entry.getIntKey() != port) {
				server.sendPacket(port, new Packet11User(true, entry.getValue()));
			}
		}
	}
	
	@ServerSide
	private void sendAllMessages(int port) {
		for(int i = 0; i < data.size(); i++) {
			Object object = data.get(i);
			if(object instanceof Message) {
				server.sendPacket(port, new Packet12Message((Message) object));
			} else if(object instanceof Packet13Image) {
				server.sendPacket(port, (Packet) object);
			}
		}
	}
	
	@ClientSide
	public void handleUser(boolean isConnected, String login) {
		if(isConnected) {
			gui.writeItalicMessage(login + " is connected, welcome!");
			gui.addToUserList(login);
		} else {
			gui.writeItalicMessage(login + " is disconnected, bye!");
			gui.removeFromUserList(login);
		}
	}
	
	@ClientSide
	public void handleDisconnection(int reason) {
		disconnect(reason, false);
	}
	
	@ServerSide
	public void handleDisconnection(int port, int reason) {
		String login = users.get(port);
		if(users.get(port) != null) {
			users.remove(port);
			server.sendBroadcast(new Packet11User(false, login));
		}
	}
	
	@ClientSide
	public void sendMessage() {
		client.sendPacket(new Packet12Message(new Message(null, gui.getMessage())));
		gui.clearMessage();
	}
	
	@ClientSide
	public void sendImage(BufferedImage image, String format) {
		client.sendPacket(new Packet13Image(image, format));
	}
	
	@ClientSide
	public void connect() {
		String address = gui.getAddress();
		int port = gui.getPort();
		String login = gui.getLogin();
		String password = gui.getPassword();
		if((address != null) && !address.isEmpty() && (port != 0)) {
			if((login != null) && !login.isEmpty() && (password != null) && !password.isEmpty()) {
				gui.setInformation("Connecting to client...");
				if(connectToServer(address, port, login, password)) {
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
	
	@ClientSide
	public void disconnect(int reason, boolean byAction) {
		if(byAction) {
			disconnectFromServer();
		} else {
			gui.setInformation("Client disconnected: " + getReasonText(reason));
			gui.setClientStatus("Disconnected");
			gui.setConnection(false);
			gui.clearUserList();
			gui.clearMessages();
		}
	}
	
	@ClientSide
	private String getReasonText(int reason) {
		switch(reason) {
		case DISCONNECT_USER:
			return "disconnect button";
		case DISCONNECT_INVALID_PASSWORD:
			return "invalid password";
		case DISCONNECT_LOGIN_EXISTS:
			return "login exists";
		default:
			return "unknown";
		}
	}
	
	public boolean isServerWorking() {
		return ((server != null) && server.isAlive());
	}
	
	public boolean isClientConnected() {
		return ((client != null) && client.isAlive());
	}
}

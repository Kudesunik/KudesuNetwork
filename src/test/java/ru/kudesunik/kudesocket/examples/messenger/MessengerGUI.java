package ru.kudesunik.kudesocket.examples.messenger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class MessengerGUI extends JFrame {
	
	private final JPanel contentPane;
	
	private final JTextField textFieldMessage;
	private final JTextField textFieldAddress;
	private final JTextField textFieldPort;
	private final JTextField textFieldLogin;
	private final JPasswordField passwordField;
	
	private final JLabel serverStatus;
	private final JLabel clientStatus;
	private final JLabel labelInformation;
	
	private final DefaultListModel<String> usersListModel;
	
	private final MessengerListener listener;
	
	private final JButton buttonConnect;
	
	public MessengerGUI(MessengerHandler handler) {
		setTitle("Messenger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		listener = new MessengerListener(this, handler);
		
		JPanel panelUsers = new JPanel();
		panelUsers.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panelUsers = new GridBagConstraints();
		gbc_panelUsers.insets = new Insets(2, 2, 2, 2);
		gbc_panelUsers.fill = GridBagConstraints.BOTH;
		gbc_panelUsers.gridx = 0;
		gbc_panelUsers.gridy = 0;
		contentPane.add(panelUsers, gbc_panelUsers);
		GridBagLayout gbl_panelUsers = new GridBagLayout();
		gbl_panelUsers.columnWidths = new int[]{0, 0};
		gbl_panelUsers.rowHeights = new int[]{0, 0};
		gbl_panelUsers.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelUsers.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelUsers.setLayout(gbl_panelUsers);
		
		panelUsers.setMinimumSize(new Dimension(40, 10));
		
		JList<String> listUsers = new JList<>();
		usersListModel = new DefaultListModel<>();
		listUsers.setModel(usersListModel);
		
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listUsers = new GridBagConstraints();
		gbc_listUsers.insets = new Insets(2, 2, 2, 2);
		gbc_listUsers.fill = GridBagConstraints.BOTH;
		gbc_listUsers.gridx = 0;
		gbc_listUsers.gridy = 0;
		panelUsers.add(listUsers, gbc_listUsers);
		
		JPanel panelText = new JPanel();
		panelText.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panelText = new GridBagConstraints();
		gbc_panelText.insets = new Insets(2, 2, 2, 2);
		gbc_panelText.fill = GridBagConstraints.BOTH;
		gbc_panelText.gridx = 1;
		gbc_panelText.gridy = 0;
		contentPane.add(panelText, gbc_panelText);
		GridBagLayout gbl_panelText = new GridBagLayout();
		gbl_panelText.columnWidths = new int[]{0, 0};
		gbl_panelText.rowHeights = new int[]{0, 0};
		gbl_panelText.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelText.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelText.setLayout(gbl_panelText);
		
		JTextPane textPaneMessages = new JTextPane();
		GridBagConstraints gbc_textPaneMessages = new GridBagConstraints();
		gbc_textPaneMessages.insets = new Insets(2, 2, 2, 2);
		gbc_textPaneMessages.fill = GridBagConstraints.BOTH;
		gbc_textPaneMessages.gridx = 0;
		gbc_textPaneMessages.gridy = 0;
		panelText.add(textPaneMessages, gbc_textPaneMessages);
		
		JPanel panelConnection = new JPanel();
		panelConnection.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panelConnection = new GridBagConstraints();
		gbc_panelConnection.insets = new Insets(2, 2, 2, 2);
		gbc_panelConnection.fill = GridBagConstraints.BOTH;
		gbc_panelConnection.gridx = 2;
		gbc_panelConnection.gridy = 0;
		contentPane.add(panelConnection, gbc_panelConnection);
		GridBagLayout gbl_panelConnection = new GridBagLayout();
		gbl_panelConnection.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panelConnection.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelConnection.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelConnection.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelConnection.setLayout(gbl_panelConnection);
		
		JLabel labelAddress = new JLabel("Address:");
		GridBagConstraints gbc_labelAddress = new GridBagConstraints();
		gbc_labelAddress.fill = GridBagConstraints.BOTH;
		gbc_labelAddress.insets = new Insets(2, 2, 2, 2);
		gbc_labelAddress.gridx = 0;
		gbc_labelAddress.gridy = 0;
		panelConnection.add(labelAddress, gbc_labelAddress);
		
		textFieldAddress = new JTextField();
		GridBagConstraints gbc_textFieldAddress = new GridBagConstraints();
		gbc_textFieldAddress.insets = new Insets(2, 2, 2, 2);
		gbc_textFieldAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAddress.gridx = 1;
		gbc_textFieldAddress.gridy = 0;
		panelConnection.add(textFieldAddress, gbc_textFieldAddress);
		textFieldAddress.setColumns(10);
		
		JLabel labelPort = new JLabel("Port:");
		GridBagConstraints gbc_labelPort = new GridBagConstraints();
		gbc_labelPort.fill = GridBagConstraints.BOTH;
		gbc_labelPort.insets = new Insets(2, 2, 2, 2);
		gbc_labelPort.gridx = 2;
		gbc_labelPort.gridy = 0;
		panelConnection.add(labelPort, gbc_labelPort);
		
		textFieldPort = new JTextField();
		GridBagConstraints gbc_textFieldPort = new GridBagConstraints();
		gbc_textFieldPort.insets = new Insets(2, 2, 2, 2);
		gbc_textFieldPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPort.gridx = 3;
		gbc_textFieldPort.gridy = 0;
		panelConnection.add(textFieldPort, gbc_textFieldPort);
		textFieldPort.setColumns(10);
		
		JLabel labelLogin = new JLabel("Login:");
		GridBagConstraints gbc_labelLogin = new GridBagConstraints();
		gbc_labelLogin.fill = GridBagConstraints.BOTH;
		gbc_labelLogin.insets = new Insets(2, 2, 2, 2);
		gbc_labelLogin.gridx = 0;
		gbc_labelLogin.gridy = 1;
		panelConnection.add(labelLogin, gbc_labelLogin);
		
		textFieldLogin = new JTextField();
		GridBagConstraints gbc_textFieldLogin = new GridBagConstraints();
		gbc_textFieldLogin.insets = new Insets(2, 2, 2, 2);
		gbc_textFieldLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLogin.gridx = 1;
		gbc_textFieldLogin.gridy = 1;
		panelConnection.add(textFieldLogin, gbc_textFieldLogin);
		textFieldLogin.setColumns(10);
		
		JLabel labelPassword = new JLabel("Password:");
		GridBagConstraints gbc_labelPassword = new GridBagConstraints();
		gbc_labelPassword.anchor = GridBagConstraints.EAST;
		gbc_labelPassword.fill = GridBagConstraints.VERTICAL;
		gbc_labelPassword.insets = new Insets(2, 2, 2, 2);
		gbc_labelPassword.gridx = 2;
		gbc_labelPassword.gridy = 1;
		panelConnection.add(labelPassword, gbc_labelPassword);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(2, 2, 2, 2);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 3;
		gbc_passwordField.gridy = 1;
		panelConnection.add(passwordField, gbc_passwordField);
		
		JButton buttonCreateServer = new JButton("Create server");
		GridBagConstraints gbc_buttonCreateServer = new GridBagConstraints();
		gbc_buttonCreateServer.insets = new Insets(2, 2, 2, 2);
		gbc_buttonCreateServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonCreateServer.gridwidth = 4;
		gbc_buttonCreateServer.gridx = 0;
		gbc_buttonCreateServer.gridy = 2;
		panelConnection.add(buttonCreateServer, gbc_buttonCreateServer);
		
		buttonCreateServer.setName("Create server");
		buttonCreateServer.addActionListener(listener);
		
		buttonConnect = new JButton("Connect");
		GridBagConstraints gbc_buttonConnect = new GridBagConstraints();
		gbc_buttonConnect.insets = new Insets(2, 2, 2, 2);
		gbc_buttonConnect.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonConnect.gridwidth = 4;
		gbc_buttonConnect.gridx = 0;
		gbc_buttonConnect.gridy = 3;
		panelConnection.add(buttonConnect, gbc_buttonConnect);
		
		buttonConnect.setName("Connect");
		
		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.fill = GridBagConstraints.BOTH;
		gbc_verticalStrut.gridwidth = 4;
		gbc_verticalStrut.insets = new Insets(2, 2, 2, 2);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 4;
		panelConnection.add(verticalStrut, gbc_verticalStrut);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 4;
		gbc_separator.insets = new Insets(2, 2, 2, 2);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 5;
		panelConnection.add(separator, gbc_separator);
		
		JLabel labelClientStatus = new JLabel("Client:");
		GridBagConstraints gbc_labelClientStatus = new GridBagConstraints();
		gbc_labelClientStatus.fill = GridBagConstraints.BOTH;
		gbc_labelClientStatus.insets = new Insets(2, 2, 2, 2);
		gbc_labelClientStatus.gridx = 0;
		gbc_labelClientStatus.gridy = 6;
		panelConnection.add(labelClientStatus, gbc_labelClientStatus);
		
		clientStatus = new JLabel("Disconnected");
		GridBagConstraints gbc_clientStatus = new GridBagConstraints();
		gbc_clientStatus.gridwidth = 3;
		gbc_clientStatus.insets = new Insets(2, 2, 2, 2);
		gbc_clientStatus.gridx = 1;
		gbc_clientStatus.gridy = 6;
		panelConnection.add(clientStatus, gbc_clientStatus);
		
		JLabel labelServerStatus = new JLabel("Server:");
		GridBagConstraints gbc_labelServerStatus = new GridBagConstraints();
		gbc_labelServerStatus.fill = GridBagConstraints.BOTH;
		gbc_labelServerStatus.insets = new Insets(2, 2, 2, 2);
		gbc_labelServerStatus.gridx = 0;
		gbc_labelServerStatus.gridy = 7;
		panelConnection.add(labelServerStatus, gbc_labelServerStatus);
		
		serverStatus = new JLabel("Stopped");
		GridBagConstraints gbc_serverStatus = new GridBagConstraints();
		gbc_serverStatus.gridwidth = 3;
		gbc_serverStatus.insets = new Insets(2, 2, 2, 2);
		gbc_serverStatus.gridx = 1;
		gbc_serverStatus.gridy = 7;
		panelConnection.add(serverStatus, gbc_serverStatus);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 4;
		gbc_separator_1.insets = new Insets(2, 2, 2, 2);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 8;
		panelConnection.add(separator_1, gbc_separator_1);
		
		labelInformation = new JLabel("Awaiting commands");
		labelInformation.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_labelInformation = new GridBagConstraints();
		gbc_labelInformation.anchor = GridBagConstraints.SOUTH;
		gbc_labelInformation.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelInformation.gridwidth = 4;
		gbc_labelInformation.insets = new Insets(2, 2, 2, 2);
		gbc_labelInformation.gridx = 0;
		gbc_labelInformation.gridy = 9;
		panelConnection.add(labelInformation, gbc_labelInformation);
		buttonConnect.addActionListener(listener);
		
		JPanel panelSend = new JPanel();
		panelSend.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panelSend = new GridBagConstraints();
		gbc_panelSend.insets = new Insets(2, 2, 2, 2);
		gbc_panelSend.gridwidth = 3;
		gbc_panelSend.fill = GridBagConstraints.BOTH;
		gbc_panelSend.gridx = 0;
		gbc_panelSend.gridy = 1;
		contentPane.add(panelSend, gbc_panelSend);
		GridBagLayout gbl_panelSend = new GridBagLayout();
		gbl_panelSend.columnWidths = new int[]{0, 0, 0};
		gbl_panelSend.rowHeights = new int[]{0, 0};
		gbl_panelSend.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panelSend.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelSend.setLayout(gbl_panelSend);
		
		JPanel panelSendField = new JPanel();
		GridBagConstraints gbc_panelSendField = new GridBagConstraints();
		gbc_panelSendField.insets = new Insets(2, 2, 2, 2);
		gbc_panelSendField.fill = GridBagConstraints.BOTH;
		gbc_panelSendField.gridx = 0;
		gbc_panelSendField.gridy = 0;
		panelSend.add(panelSendField, gbc_panelSendField);
		GridBagLayout gbl_panelSendField = new GridBagLayout();
		gbl_panelSendField.columnWidths = new int[]{0, 0};
		gbl_panelSendField.rowHeights = new int[]{0, 0};
		gbl_panelSendField.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelSendField.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelSendField.setLayout(gbl_panelSendField);
		
		textFieldMessage = new JTextField();
		GridBagConstraints gbc_textFieldMessage = new GridBagConstraints();
		gbc_textFieldMessage.insets = new Insets(2, 2, 2, 2);
		gbc_textFieldMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMessage.gridx = 0;
		gbc_textFieldMessage.gridy = 0;
		panelSendField.add(textFieldMessage, gbc_textFieldMessage);
		textFieldMessage.setColumns(10);
		
		JPanel panelSendButton = new JPanel();
		GridBagConstraints gbc_panelSendButton = new GridBagConstraints();
		gbc_panelSendButton.insets = new Insets(2, 2, 2, 2);
		gbc_panelSendButton.fill = GridBagConstraints.BOTH;
		gbc_panelSendButton.gridx = 1;
		gbc_panelSendButton.gridy = 0;
		panelSend.add(panelSendButton, gbc_panelSendButton);
		GridBagLayout gbl_panelSendButton = new GridBagLayout();
		gbl_panelSendButton.columnWidths = new int[]{0, 0, 0};
		gbl_panelSendButton.rowHeights = new int[]{0, 0};
		gbl_panelSendButton.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panelSendButton.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelSendButton.setLayout(gbl_panelSendButton);
		
		JButton buttonSend = new JButton("Send");
		GridBagConstraints gbc_buttonSend = new GridBagConstraints();
		gbc_buttonSend.insets = new Insets(2, 2, 2, 2);
		gbc_buttonSend.gridx = 0;
		gbc_buttonSend.gridy = 0;
		panelSendButton.add(buttonSend, gbc_buttonSend);
		
		buttonSend.setName("Send");
		buttonSend.addActionListener(listener);
		
		JButton buttonImage = new JButton("Image");
		GridBagConstraints gbc_buttonImage = new GridBagConstraints();
		gbc_buttonImage.insets = new Insets(2, 2, 2, 2);
		gbc_buttonImage.gridx = 1;
		gbc_buttonImage.gridy = 0;
		panelSendButton.add(buttonImage, gbc_buttonImage);
		
		buttonImage.setName("Image");
		buttonImage.addActionListener(listener);
	}
	
	public String getMessage() {
		return textFieldMessage.getText();
	}
	
	public String getAddress() {
		return textFieldAddress.getText();
	}
	
	public int getPort() {
		try {
			return Integer.parseInt(textFieldPort.getText());
		} catch(NumberFormatException ex) {
			return 0;
		}
	}
	
	public String getLogin() {
		return textFieldLogin.getText();
	}
	
	public String getPassword() {
		return new String(passwordField.getPassword());
	}
	
	public void setInformation(String text) {
		labelInformation.setText(text);
	}
	
	public void setServerStatus(String text) {
		serverStatus.setText(text);
	}
	
	public void setClientStatus(String text) {
		clientStatus.setText(text);
	}
	
	public void setConnection(boolean isConnected) {
		if(isConnected) {
			buttonConnect.setText("Disconnect");
		} else {
			buttonConnect.setText("Connect");
		}
	}
}

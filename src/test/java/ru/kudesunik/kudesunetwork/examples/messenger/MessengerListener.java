package ru.kudesunik.kudesunetwork.examples.messenger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MessengerListener implements ActionListener {
	
	private MessengerHandler handler;
	
	public void attachHandler(MessengerHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if(source instanceof JButton) {
			JButton button = (JButton) source;
			switch(button.getName()) {
			case "Start server":
				if(!handler.isServerWorking()) {
					handler.startServer();
				} else {
					handler.stopServer();
				}
				break;
			case "Connect":
				if(handler.isClientConnected()) {
					handler.disconnect(MessengerHandler.DISCONNECT_USER, true);
				} else {
					handler.connect();
				}
				break;
			case "Send":
				if(handler.isClientConnected()) {
					handler.sendMessage();
				}
				break;
			case "Image":
				sendImage();
				break;
			default:
				break;
			}
		}
	}
	
	private void sendImage() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "png", "jpg");
		fileChooser.addChoosableFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String extension = getFileExtension(selectedFile.getAbsolutePath()).orElse("");
			if(!extension.isEmpty()) {
				try {
					handler.sendImage(ImageIO.read(selectedFile), extension);
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private Optional<String> getFileExtension(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
	
	@SuppressWarnings("serial")
	public class MessengerEnterListener extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(handler.isClientConnected()) {
				handler.sendMessage();
			}
		}
	}
}

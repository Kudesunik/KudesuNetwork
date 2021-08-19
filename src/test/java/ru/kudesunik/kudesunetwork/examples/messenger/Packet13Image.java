package ru.kudesunik.kudesunetwork.examples.messenger;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import ru.kudesunik.kudesunetwork.packet.Packet;

public class Packet13Image implements Packet {
	
	public static final int ID = 13;
	
	private String format;
	private BufferedImage image;
	
	public Packet13Image(BufferedImage image, String format) {
		this.image = image;
		this.format = format;
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(format);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, format, outputStream);
		byte[] array = outputStream.toByteArray();
		data.writeInt(array.length);
		data.write(array);
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		format = data.readUTF();
		byte[] array = new byte[data.readInt()];
		data.read(array, 0, array.length);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
		image = ImageIO.read(inputStream);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public String getFormat() {
		return format;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		return (Packet) super.clone();
	}
}

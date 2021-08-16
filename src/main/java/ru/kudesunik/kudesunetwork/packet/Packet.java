package ru.kudesunik.kudesunetwork.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet extends Cloneable {
	
	public byte getId();
	
	public void write(DataOutputStream data) throws IOException;
	
	public void read(DataInputStream data) throws IOException;
	
	public Packet clone() throws CloneNotSupportedException;
}

package ru.kudesunik.kudesunetwork.util;

import java.io.IOException;
import java.io.InputStream;

public class Utilities {
	
	public static String concat(String... strings) {
		int length = 0;
		for(int i = 0; i < strings.length; ++i) {
			length += strings[i].length();
		}
		StringBuilder buffer = new StringBuilder(length);
		for(int i = 0; i < strings.length; ++i) {
			buffer.append(strings[i]);
		}
		return buffer.toString();
	}
	
	public static String concat(Object... objects) {
		String[] strings = new String[objects.length];
		int length = 0;
		for(int i = 0; i < objects.length; ++i) {
			String s = objects[i].toString();
			length += s.length();
			strings[i] = s;
		}
		StringBuilder buffer = new StringBuilder(length);
		for(int i = 0; i < strings.length; ++i) {
			buffer.append(strings[i]);
		}
		return buffer.toString();
	}
	
	public static int readBytes(InputStream inputStream, byte[] array, int offset, int length) throws IOException {
		int n = 0;
		while(n < length) {
			int count = inputStream.read(array, offset + n, length - n);
			if(count < 0) {
				break;
			}
			n += count;
		}
		return n;
	}
	
	public static byte[] compress(byte[] data) {
		return data; //TODO: Implementation
	}
	
	public static byte[] decompress(byte[] data) {
		return data; //TODO: Implementation
	}
	
	private Utilities() {
		//Class instantiation not allowed
	}
}

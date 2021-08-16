package ru.kudesunik.kudesunetwork.util;

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
	
	private Utilities() {
		//Class instantiation not allowed
	}
}

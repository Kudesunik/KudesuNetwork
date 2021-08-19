package ru.kudesunik.kudesunetwork;

public enum KudesuNetworkFlags {
	
	ENCRYPTED(1), COMPRESSED(2), RESERVED_1(4), RESERVED_2(8), RESERVED_3(16), RESERVED_4(32), RESERVED_5(64), RESERVED_6(128);
	
	private final int flag;
	
	private KudesuNetworkFlags(int flag) {
		this.flag = flag;
	}
	
	public static int appendFlag(int currentFlag, KudesuNetworkFlags flag) {
		return (currentFlag |= flag.flag);
	}
	
	public static boolean checkFlag(int currentFlag, KudesuNetworkFlags flag) {
		return (currentFlag & flag.flag) == 1;
	}
}

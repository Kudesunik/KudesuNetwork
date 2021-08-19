package ru.kudesunik.kudesunetwork.util;

public class Combiner {
	
	private static final int BYTE_MASK = 0xFF;
	private static final int BYTE_SHIFT = 8;
	private static final int SHORT_MASK = 0xFFFF;
	private static final int SHORT_SHIFT = 16;
	private static final long INT_MASK = 0xFFFFFFFFL;
	private static final int INT_SHIFT = 32;
	
	public static short combineBytes(byte b1, byte b2) {
		return (short) ((b1 << BYTE_SHIFT) | (b2 & BYTE_MASK));
	}
	
	public static byte[] decombineShort(short s, byte[] array) {
		if((array == null) || (array.length < 2)) {
			array = new byte[2];
		}
		array[0] = (byte) (s >> BYTE_SHIFT);
		array[1] = (byte) (s & BYTE_MASK);
		return array;
	}
	
	public static int combineShorts(short s1, short s2) {
		return (s1 << SHORT_SHIFT) | (s2 & SHORT_MASK);
	}
	
	public static long combineShorts(short s1, short s2, short s3, short s4) {
		return combineInts((s1 << SHORT_SHIFT) | (s2 & SHORT_MASK), (s3 << SHORT_SHIFT) | (s4 & SHORT_MASK));
	}
	
	public static short[] decombineLongToShorts(long l, short[] array) {
		if((array == null) || (array.length < 4)) {
			array = new short[4];
		}
		decombineInt((int) (l & INT_MASK), array);
		array[2] = array[0];
		array[3] = array[1];
		decombineInt((int) (l >> INT_SHIFT), array);
		return array;
	}
	
	public static short[] decombineInt(int i, short[] array) {
		if((array == null) || (array.length < 2)) {
			array = new short[2];
		}
		array[0] = (short) (i >> SHORT_SHIFT);
		array[1] = (short) (i & SHORT_MASK);
		return array;
	}
	
	public static long combineInts(int i1, int i2) {
		return (((long) i1) << INT_SHIFT) | (i2 & INT_MASK);
	}
	
	public static int[] decombineLongToInts(long l, int[] array) {
		if((array == null) || (array.length < 2)) {
			array = new int[2];
		}
		array[0] = (int) (l >> INT_SHIFT);
		array[1] = (int) (l & INT_MASK);
		return array;
	}
	
	public static long combineFloats(float f1, float f2) {
		return (((long) Float.floatToIntBits(f1)) << INT_SHIFT) | (Float.floatToIntBits(f2) & INT_MASK);
	}
	
	public static float[] decombineLongToFloats(long l, float[] array) {
		if((array == null) || (array.length < 2)) {
			array = new float[2];
		}
		array[0] = Float.intBitsToFloat((int) (l >> INT_SHIFT));
		array[1] = Float.intBitsToFloat((int) (l & INT_MASK));
		return array;
	}
	
	public static boolean checkBounds(long l1, long l2, Class<? extends Number> numberType, boolean throwException) {
		boolean checkFlag = true;
		if(numberType == Byte.TYPE) {
			checkFlag = ((l1 >= Byte.MIN_VALUE) && (l1 <= Byte.MAX_VALUE) && (l2 >= Byte.MIN_VALUE) && (l2 <= Byte.MAX_VALUE));
		} else if(numberType == Short.TYPE) {
			checkFlag = ((l1 >= Short.MIN_VALUE) && (l1 <= Short.MAX_VALUE) && (l2 >= Short.MIN_VALUE) && (l2 <= Short.MAX_VALUE));
		} else if(numberType == Integer.TYPE) {
			checkFlag = ((l1 >= Integer.MIN_VALUE) && (l1 <= Integer.MAX_VALUE) && (l2 >= Integer.MIN_VALUE) && (l2 <= Integer.MAX_VALUE));
		} else if(numberType == Long.TYPE) {
			return true;
		} else {
			checkFlag = false;
		}
		if(!checkFlag) {
			if(throwException) {
				throw new IllegalArgumentException("Check bounds exception: '" + l1 + "' or '" + l2 + "' out of bounds of type '" + numberType.getSimpleName() + "'");
			}
			return false;
		}
		return true;
	}
	
	private Combiner() {
		//Class instantiation not allowed
	}
}

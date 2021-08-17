package ru.kudesunik.kudesunetwork.util;

import java.util.List;
import java.util.Random;

public class RandomMath {
	
	private static final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);
	
	public static int getRandomBetween(int i1, int i2, Random random) {
		int minValue = Math.min(i1, i2);
		return minValue + random.nextInt(Math.max(i1, i2) - minValue + 1);
	}
	
	public static int getRandomBetween(int i1, int i2) {
		return getRandomBetween(i1, i2, random.get());
	}
	
	public static int getRandomBetween(int i1, int i2, long seed) {
		Random localRandom = new Random(seed);
		return getRandomBetween(i1, i2, localRandom);
	}
	
	public static double getRandomBetween(double d1, double d2, Random random) {
		double minValue = Math.min(d1, d2);
		return minValue + random.nextDouble() * (Math.max(d1, d2) - minValue);
	}
	
	public static double getRandomBetween(double d1, double d2) {
		return getRandomBetween(d1, d2, random.get());
	}
	
	public static float getRandomBetween(float f1, float f2, Random random) {
		return (float) getRandomBetween((double) f1, f2, random);
	}
	
	public static float getRandomBetween(float f1, float f2) {
		return (float) getRandomBetween((double) f1, f2);
	}
	
	public static long nextLong() {
		return random.get().nextLong();
	}
	
	public static double nextDouble() {
		return random.get().nextDouble();
	}
	
	public static boolean checkChance(double chance, Random random) {
		return random.nextDouble() < chance;
	}
	
	public static boolean checkChance(double chance) {
		return random.get().nextDouble() < chance;
	}
	
	public static boolean checkChance(double chanceMin, double chanceMax, Random random) {
		return (random.nextDouble() >= chanceMin) && (random.nextDouble() < chanceMax);
	}
	
	public static boolean checkChance(double chanceMin, double chanceMax) {
		return (random.get().nextDouble() >= chanceMin) && (random.get().nextDouble() < chanceMax);
	}
	
	public static <T> T getRandomObjectFromList(List<T> objects) {
		if((objects != null) && !objects.isEmpty()) {
			return objects.get(getRandomBetween(0, objects.size() - 1));
		}
		return null;
	}
	
	private RandomMath() {
		//Class instantiation not allowed
	}
}

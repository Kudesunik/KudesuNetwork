package ru.kudesunik.kudesunetwork.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import ru.kudesunik.kudesunetwork.annotations.Nullable;

public class NetworkCipher {
	
	public static final String SALT = "WpGPL5cmscJfBf7PTYEzgSuz4z5zZUS8";
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	
	private static final int ITERATION_COUNT = 65536;
	private static final int KEY_LENGTH = 256;
	
	private final SecretKey key;
	
	public NetworkCipher(String password) {
		this.key = getKeyFromPassword(password, SALT);
	}
	
	public byte[] encrypt(byte[] data, IvParameterSpec iv) {
		return encrypt(ALGORITHM, data, key, iv);
	}
	
	public byte[] decrypt(byte[] data, IvParameterSpec iv) {
		return decrypt(ALGORITHM, data, key, iv);
	}
	
	public static SecretKey getKeyFromPassword(String password, String salt) {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
			return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch(NoSuchAlgorithmException | InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static IvParameterSpec generateIV() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}
	
	public static @Nullable byte[] encrypt(String algorithm, byte[] data, SecretKey key, IvParameterSpec iv) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return cipher.doFinal(data);
		} catch(InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static @Nullable byte[] decrypt(String algorithm, byte[] data, SecretKey key, IvParameterSpec iv) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			return cipher.doFinal(data);
		} catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}

package com.caiyongji;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtil {
	private static final String ENCODED_UTF8 = "UTF-8";
	private static final String ENCODED_ASCII = "ASCII";
	private static final String CIPHER_INSTANCE_CBC = "DES/CBC/PKCS5Padding";

	public static String encrypt(String HexString, String keyStr) throws Exception {
		String jmstr = "";
		try {
			byte[] theKey = null;
			String jqstr = keyStr.substring(0, 8).toUpperCase();
			theKey = jqstr.getBytes(ENCODED_ASCII);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_CBC);
			DESKeySpec desKeySpec = new DESKeySpec(theKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(theKey);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] theCph = cipher.doFinal(HexString.getBytes(ENCODED_UTF8));
			jmstr = toHexString(theCph).toUpperCase();
			jmstr = toHexString(theCph);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jmstr.toUpperCase();
	}

	private static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}

		return hexString.toString();
	}

	public static String decrypt(String message, String key) {

		try {
			byte[] bytesrc = convertHexString(message);
			byte[] theKey = null;
			String jqstr = key.substring(0, 8).toUpperCase();
			theKey = jqstr.getBytes(ENCODED_ASCII);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_CBC);
			DESKeySpec desKeySpec = new DESKeySpec(theKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(theKey);

			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

			byte[] retByte = cipher.doFinal(bytesrc);
			return new String(retByte, ENCODED_UTF8);
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR_SIGNATURE + e.getMessage();
		}
	}

	private static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	public static void main(String[] args) throws Exception {
		String key = "caiyongji";
		String value = "中文";

		System.out.println("before encrypt:" + value);
		String a = encrypt(value, key);

		System.out.println("after encrypt:" + a);
		String b = decrypt(a, key);
		System.out.println("decrypt:" + b);

	}
}
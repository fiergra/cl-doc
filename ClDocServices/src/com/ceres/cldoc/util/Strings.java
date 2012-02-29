package com.ceres.cldoc.util;

import java.security.MessageDigest;

public class Strings {

	public static String transcribe(String string) {
		return string != null ? string.toUpperCase() : null;
	}

	private static String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static String hash(String string) {
		if (string == null || string.length() == 0) {
			return null;
		} else {
			byte[] bytesOfMessage;
			try {
				bytesOfMessage = string.getBytes("UTF-8");
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] thedigest = md.digest(bytesOfMessage);
				return getHexString(thedigest);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}

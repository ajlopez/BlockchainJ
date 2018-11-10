package com.ajlopez.blockchain.utils;

/**
 * Created by ajlopez on 10/11/2018.
 */
public class HexUtils {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private HexUtils() { }

    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    public static String bytestoHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xff;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}

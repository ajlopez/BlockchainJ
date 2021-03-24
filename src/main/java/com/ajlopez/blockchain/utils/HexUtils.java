package com.ajlopez.blockchain.utils;

/**
 * Created by ajlopez on 10/11/2018.
 */
public class HexUtils {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private HexUtils() { }

    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    public static String bytesToHexString(byte[] bytes) {
        return bytesToHexString(bytes, false, false);
    }

    public static String bytesToHexString(byte[] bytes, boolean prefix, boolean removeLeadingZeroes) {
        // TODO simplify prefix
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xff;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        String result = new String(hexChars);

        if (!removeLeadingZeroes)
            if (prefix)
                return "0x" + result;
            else
                return result;

        for (int k = 0; k < result.length(); k++)
            if (result.charAt(k) != '0')
                if (prefix)
                    return "0x" + result.substring(k);
                else
                    return result.substring(k);

        if (prefix)
            return "0x0";

        return "0";
    }

    // https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] hexStringToBytes(String s) {
        if (s.startsWith("0x") || s.startsWith("0X"))
            s = s.substring(2);

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

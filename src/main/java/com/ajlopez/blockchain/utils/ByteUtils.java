package com.ajlopez.blockchain.utils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class ByteUtils {
    public static byte[] longToBytes(long value) {
        byte[] result = new byte[Long.BYTES];

        for (int k = Long.BYTES; k-- > 0;) {
            result[k] = (byte)(value & 0xff);
            value >>= 8;
        }

        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;

        for (int k = 0; k < bytes.length; k++) {
            result <<= 8;
            result |= bytes[k] & 0xff;
        }

        return result;
    }
}

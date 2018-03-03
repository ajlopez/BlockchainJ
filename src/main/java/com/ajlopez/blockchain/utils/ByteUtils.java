package com.ajlopez.blockchain.utils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class ByteUtils {
    private ByteUtils() { }

    public static byte[] unsignedLongToBytes(long value) {
        byte[] result = new byte[Long.BYTES];

        for (int k = Long.BYTES; k-- > 0;) {
            result[k] = (byte)(value & 0xff);
            value >>= 8;
        }

        return result;
    }

    public static long bytesToUnsignedLong(byte[] bytes) {
        long result = 0;

        for (int k = 0; k < bytes.length && k < Long.BYTES; k++) {
            result <<= 8;
            result |= bytes[k] & 0xff;
        }

        return result;
    }

    public static byte[] unsignedIntegerToBytes(int value) {
        byte[] result = new byte[Integer.BYTES];

        for (int k = Integer.BYTES; k-- > 0;) {
            result[k] = (byte)(value & 0xff);
            value >>= 8;
        }

        return result;
    }

    public static short bytesToUnsignedShort(byte[] bytes, int offset) {
        short result = 0;

        for (int k = 0; k < Short.BYTES; k++) {
            result <<= 8;
            result |= bytes[k + offset] & 0xff;
        }

        return result;
    }

    public static short bytesWithLengthToUnsignedInteger(byte[] bytes, int offset) {
        short result = 0;
        short l = bytes[offset];

        for (int k = 0; k < l; k++) {
            result <<= 8;
            result |= bytes[k + offset + 1] & 0xff;
        }

        return result;
    }

    public static int bytesToUnsignedInteger(byte[] bytes, int offset) {
        int result = 0;

        for (int k = 0; k < Integer.BYTES && k + offset < bytes.length; k++) {
            result <<= 8;
            result |= bytes[k + offset] & 0xff;
        }

        return result;
    }

    public static byte[] unsignedShortToBytes(short value) {
        byte[] result = new byte[Short.BYTES];

        for (int k = Short.BYTES; k-- > 0;) {
            result[k] = (byte)(value & 0xff);
            value >>= 8;
        }

        return result;
    }

    public static boolean areZero(byte[] bytes) {
        for (int k = 0; k < bytes.length; k++)
            if (bytes[k] != 0)
                return false;

        return true;
    }
}

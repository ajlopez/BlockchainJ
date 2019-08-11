package com.ajlopez.blockchain.utils;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class ByteUtils {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final byte[] ZERO_BYTE_ARRAY = new byte[1];

    private ByteUtils() { }

    public static boolean equals(byte[] bytes, int offset, byte[] bytes2, int offset2, int length) {
        for (int k = 0; k < length; k++)
            if (bytes[k + offset] != bytes2[k + offset2])
                return false;

        return true;
    }

    public static byte[] normalizeBytesToNull(byte[] bytes) {
        if (isNullOrEmpty(bytes))
            return null;

        return bytes;
    }

    public static boolean isNullOrEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    public static byte[] unsignedLongToBytes(long value) {
        if (value < 0)
            throw new IllegalArgumentException("Invalid negative value");

        byte[] result = new byte[Long.BYTES];

        for (int k = Long.BYTES; value != 0 && k-- > 0;) {
            result[k] = (byte)(value & 0xff);
            value >>= 8;
        }

        return result;
    }

    public static byte[] unsignedLongToNormalizedBytes(long value) {
        return normalizedBytes(unsignedLongToBytes(value));
    }

    public static long bytesToUnsignedLong(byte[] bytes) {
        long result = 0;

        for (int k = 0; k < bytes.length && k < Long.BYTES; k++) {
            result <<= 8;
            result |= bytes[k] & 0xff;
        }

        if (result < 0)
            throw new IllegalArgumentException("Invalid negative value");

        return result;
    }

    public static byte[] unsignedIntegerToBytes(int value) {
        byte[] result = new byte[Integer.BYTES];

        unsignedIntegerToBytes(value, result, 0);

        return result;
    }

    public static byte[] unsignedIntegerToNormalizedBytes(int value) {
        return normalizedBytes(unsignedIntegerToBytes(value));
    }

    public static void unsignedIntegerToBytes(int value, byte[] bytes, int offset) {
        if (value < 0)
            throw new IllegalArgumentException("Invalid negative value");

        for (int k = Integer.BYTES; value != 0 && k-- > 0;) {
            bytes[k + offset] = (byte)(value & 0xff);
            value >>= 8;
        }
    }

    public static short bytesToShort(byte[] bytes, int offset) {
        short result = 0;

        for (int k = 0; k < Short.BYTES && k + offset < bytes.length; k++) {
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

    public static int bytesToUnsignedInteger(byte[] bytes) {
        return bytesToUnsignedInteger(bytes, 0);
    }

    public static int bytesToUnsignedInteger(byte[] bytes, int offset) {
        int result = 0;

        for (int k = 0; k < Integer.BYTES && k + offset < bytes.length; k++) {
            result <<= 8;
            result |= bytes[k + offset] & 0xff;
        }

        if (result < 0)
            throw new IllegalArgumentException("Invalid negative value");

        return result;
    }

    public static byte[] shortToBytes(short value) {
        byte[] result = new byte[Short.BYTES];

        for (int k = Short.BYTES; value != 0 && k-- > 0;) {
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

    public static byte[] copyBytes(byte[] bytes) {
        return copyBytes(bytes, bytes.length, false, false);
    }

    public static byte[] copyBytes(byte[] bytes, int length) {
        return copyBytes(bytes, length, false, false);
    }

    public static byte[] copyBytes(byte[] bytes, int length, boolean rightPadding, boolean signed) {
        byte[] newbytes = new byte[length];

        System.arraycopy(bytes, 0, newbytes, length <= bytes.length || rightPadding ? 0 : length - bytes.length, Math.min(length, bytes.length));

        if (signed && bytes[0] < 0)
            for (int k = 0; k < length - bytes.length; k++)
                newbytes[k] = (byte)0xff;

        return newbytes;
    }

    public static int numberOfLeadingZeroes(byte[] bytes) {
        int nbytes = bytes.length;

        for (int n = 0; n < nbytes; n++)
            if (bytes[n] != 0)
                return n;

        return nbytes;
    }

    public static byte[] removeLeadingZeroes(byte[] bytes) {
        int nbytes = bytes.length;
        int nzeroes = numberOfLeadingZeroes(bytes);

        if (nzeroes == nbytes)
            return EMPTY_BYTE_ARRAY;

        if (nzeroes == 0)
            return bytes;

        byte[] newbytes = new byte[nbytes - nzeroes];
        System.arraycopy(bytes, nzeroes, newbytes, 0, newbytes.length);

        return newbytes;
    }

    public static byte[] normalizedBytes(byte[] bytes) {
        byte[] newbytes = removeLeadingZeroes(bytes);

        if (newbytes.length == 0)
            return ZERO_BYTE_ARRAY;

        return newbytes;
    }

    public static byte[] shiftLeft(byte[] bytes, int shift) {
        byte[] newbytes = new byte[bytes.length];

        int nbytes = shift / 8;

        if (nbytes >= bytes.length)
            return newbytes;

        int nbits = shift % 8;

        System.arraycopy(bytes, nbytes, newbytes, 0, bytes.length - nbytes);

        if (nbits > 0)
            for (int k = 0; k < newbytes.length; k++) {
                byte b = newbytes[k];
                newbytes[k] <<= nbits;

                if (k == 0)
                    continue;

                newbytes[k - 1] |= (b & 0xff) >>> (8 - nbits);
            }

        return newbytes;
    }

    public static byte[] shiftRight(byte[] bytes, int shift) {
        byte[] newbytes = new byte[bytes.length];

        int nbytes = shift / 8;

        if (nbytes >= bytes.length)
            return newbytes;

        int nbits = shift % 8;

        System.arraycopy(bytes, 0, newbytes, nbytes, bytes.length - nbytes);

        if (nbits > 0)
            for (int k = newbytes.length - 1; k >= 0; k--) {
                byte b = newbytes[k];
                newbytes[k] = (byte)((newbytes[k] & 0xff) >> nbits);

                if (k == newbytes.length - 1)
                    continue;

                newbytes[k + 1] |= (b & 0xff) << (8 - nbits);
            }

        return newbytes;
    }

    public static byte[] shiftArithmeticRight(byte[] bytes, int shift) {
        byte[] newbytes = new byte[bytes.length];

        int nbytes = shift / 8;

        if (nbytes >= bytes.length)
            return newbytes;

        int nbits = shift % 8;

        System.arraycopy(bytes, 0, newbytes, nbytes, bytes.length - nbytes);

        if (nbits > 0)
            for (int k = newbytes.length - 1; k >= 0; k--) {
                byte b = newbytes[k];
                newbytes[k] = (byte)((newbytes[k] & 0xff) >> nbits);

                if (k == newbytes.length - 1)
                    continue;

                newbytes[k + 1] |= (b & 0xff) << (8 - nbits);
            }

        if ((bytes[0] & 0x80) != 0) {
            for (int k = 0; k < nbytes; k++)
                newbytes[k] = (byte)0xff;

            if (nbits > 0)
                newbytes[nbytes] |= (byte)(0xff << (8 - nbits));
        }

        return newbytes;
    }
}

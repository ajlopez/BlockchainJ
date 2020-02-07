package com.ajlopez.blockchain.state;

import java.util.Arrays;

/**
 * Created by ajlopez on 18/01/2020.
 */
public class TrieKeyUtils {
    public static int getOffset(byte[] key, int position) {
        if (position % 2 == 0)
            return (key[position / 2] >> 4) & 0x0f;

        return key[position / 2] & 0x0f;
    }

    public static byte[] getSubKey(byte[] key, int from, int length) {
        byte[] subkey = new byte[(length + 1) / 2];

        if (from % 2 == 0)
            for (int k = 0; k < length / 2; k++)
                subkey[k] = key[from / 2 + k];
        else
            for (int k = 0; k < length; k++) {
                int position = from + k;
                int offset = getOffset(key, position);

                if (k % 2 == 0)
                    subkey[k / 2] = (byte)(offset << 4);
                else
                    subkey[k / 2] |= (byte)offset;
            }

        if (length % 2 == 1)
            subkey[subkey.length - 1] = (byte)(getOffset(key, from + length - 1) << 4);

        return subkey;
    }

    public static int getSharedLength(byte[] sharedKey, int sharedKeyLength, byte[] key, int position) {
        int l = 0;
        int keylength = key.length;

        if (position % 2 == 0) {
            while (l < sharedKeyLength) {
                if (position + l >= keylength * 2)
                    break;

                if (sharedKey[l/2] != key[(position + l)/2])
                    break;

                l += 2;
            }

            // TODO review these checks, more test use cases
            if (l < sharedKeyLength)
                if (getOffset(sharedKey, l) == getOffset(key, position + l))
                    return l + 1;

            return l;
        }
        else
            while (l < sharedKeyLength)
                if (getOffset(sharedKey, l) == getOffset(key, position + l))
                    l++;
                else
                    break;

        return l;
    }

    public static int concatenateKeysLength(int lengthKey1, int lengthKey2) {
        return lengthKey1 + 1 + lengthKey2;
    }

    public static byte[] concatenateKeys(byte[] key1, int lengthKey1, int offset, byte[] key2, int lengthKey2) {
        int rlength = concatenateKeysLength(lengthKey1, lengthKey2);

        byte[] result = new byte[(rlength + 1)/2];

        if (key1 != null)
            System.arraycopy(key1, 0, result, 0, key1.length);

        if (rlength % 2 == 0)
            result[result.length - 1] |= (byte)offset;
        else
            result[result.length - 1] |= (byte)(offset << 4);

        return result;
    }
}

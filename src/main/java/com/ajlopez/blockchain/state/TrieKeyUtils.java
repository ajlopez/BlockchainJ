package com.ajlopez.blockchain.state;

/**
 * Created by ajlopez on 18/01/2020.
 */
public class TrieKeyUtils {
    public static int getOffset(byte[] key, int position) {
        if (position % 2 == 0)
            return (key[position / 2] >> 4) & 0x0f;

        return key[position / 2] & 0x0f;
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Bloom;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajlopez on 21/04/2020.
 */
public class BloomEncoder {
    private BloomEncoder() {

    }

    public static byte[] encode(Bloom bloom) {
        return RLP.encode(bloom.getBytes());
    }

    public static Bloom decode(byte[] encoded) {
        byte[] bytes = RLP.decode(encoded);

        return new Bloom(bytes);
    }

    public static byte[] encodeNonZero(Bloom bloom) {
        byte[] bytes = bloom.getBytes();
        byte[] nonzero = new byte[Bloom.BLOOM_BYTES * 2];
        int n = 0;

        for (int k = 0; k < bytes.length; k++)
            if (bytes[k] != 0) {
                nonzero[n++] = (byte)k;
                nonzero[n++] = bytes[k];
            }

        return Arrays.copyOf(nonzero, n);
    }
}

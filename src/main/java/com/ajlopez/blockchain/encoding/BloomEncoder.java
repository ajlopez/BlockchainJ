package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Bloom;

import java.util.Arrays;

/**
 * Created by ajlopez on 21/04/2020.
 */
public class BloomEncoder {
    private BloomEncoder() {

    }

    public static byte[] encode(Bloom bloom) {
        byte[] bytes = bloom.getBytes();
        byte[] bytes2 = encodeNonZero(bloom);

        if (bytes2.length < Bloom.BLOOM_BYTES)
            return RLP.encode(bytes2);

        return RLP.encode(bytes);
    }

    public static Bloom decode(byte[] encoded) {
        byte[] bytes = RLP.decode(encoded);

        if (bytes.length < Bloom.BLOOM_BYTES)
            return decodeNonZero(bytes);

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

    public static Bloom decodeNonZero(byte[] encoded) {
        Bloom bloom = new Bloom();

        for (int k = 0; k < encoded.length; k += 2) {
            int position = encoded[k] & 0xff;
            int values = encoded[k + 1] & 0xff;

            for (int j = 0; j < 8; j++) {
                if ((values & 0x80) != 0)
                    bloom.add(position * 8 + j);

                values <<= 1;
            }
        }

        return bloom;
    }
}

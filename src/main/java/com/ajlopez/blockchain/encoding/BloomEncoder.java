package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Bloom;

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
}

package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 10/04/2020.
 */
public class Bloom {
    public static final int BLOOM_BYTES = 256;
    public static final int BLOOM_BITS = BLOOM_BYTES * 8;

    private final byte[] data = new byte[BLOOM_BYTES];

    public Bloom() {
    }

    public Bloom(byte[] data) {
        System.arraycopy(data, 0, this.data, 0, BLOOM_BYTES);
    }

    public int size() {
        int count = 0;

        for (int k = 0; k < BLOOM_BYTES; k++) {
            byte b = this.data[k];

            if (b == 0)
                continue;

            for (int j = 0; j < 8; j++) {
                if ((b & 1) == 1)
                    count++;

                b >>= 1;
            }
        }

        return count;
    }

    public void add(int nelement) {
        if (nelement < 0 || nelement >= BLOOM_BITS)
            throw new IllegalArgumentException("Invalid bloom element");

        int position = nelement / 8;
        int offset = 7 - (nelement % 8);

        data[position] |= 1 << offset;
    }

    public boolean include(Bloom bloom) {
        byte[] bdata = bloom.data;

        for (int k = 0; k < BLOOM_BYTES; k++)
            if ((bdata[k] & this.data[k]) != bdata[k])
                return false;

        return true;
    }

    public byte[] getBytes() {
        return this.data;
    }
}

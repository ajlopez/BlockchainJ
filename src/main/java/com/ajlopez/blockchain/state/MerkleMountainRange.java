package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * Created by ajlopez on 25/07/2019.
 */
public class MerkleMountainRange {
    private static final int NBITS = 64;

    private final Hash rootHash;
    private final long count;
    private final Hash[] treeHashes;

    public MerkleMountainRange() {
        this(null, 0, new Hash[NBITS]);
    }

    private MerkleMountainRange(Hash rootHash, long count, Hash[] treeHashes) {
        this.rootHash = rootHash;
        this.count = count;
        this.treeHashes = treeHashes;
    }

    public Hash getRootHash() { return this.rootHash; }

    public long getCount() { return this.count; }

    public MerkleMountainRange addHash(Hash hash) {
        Hash result = hash;
        int k = 0;
        long l = this.count + 1;
        Hash[] newTreeHashes = new Hash[NBITS];
        System.arraycopy(this.treeHashes, 0, newTreeHashes, 0, NBITS);

        for (; k < newTreeHashes.length && l > 0; k++, l >>= 1)
            if (newTreeHashes[k] == null) {
                newTreeHashes[k] = result;
                break;
            } else {
                result = calculate(newTreeHashes[k], result);
                newTreeHashes[k] = null;
            }

        for (k++, l >>= 1; k < newTreeHashes.length && l > 0; k++, l >>= 1)
            if (newTreeHashes[k] != null)
                result = calculate(newTreeHashes[k], result);

        return new MerkleMountainRange(result, count + 1, newTreeHashes);
    }

    private static Hash calculate(Hash hash1, Hash hash2) {
        return HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
    }
}

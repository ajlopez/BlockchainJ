package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * Created by ajlopez on 25/07/2019.
 */
public class MerkleMontainRange {
    private final Hash[] treeHashes = new Hash[64];

    public Hash addHash(Hash hash) {
        Hash result = hash;
        int k = 0;

        for (; k < treeHashes.length; k++)
            if (treeHashes[k] == null) {
                treeHashes[k] = result;
                break;
            }
            else {
                result = calculate(treeHashes[k], result);
                treeHashes[k] = null;
            }

        for (k++; k < treeHashes.length; k++)
            if (treeHashes[k] != null)
                result = calculate(treeHashes[k], result);

        return result;
    }

    private static Hash calculate(Hash hash1, Hash hash2) {
        return HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
    }
}

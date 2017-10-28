package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeader {
    private static Hash emptyHash = new Hash(new byte[32]);

    private long number;
    private Hash parentHash;
    private Hash hash;

    public BlockHeader(long number, Hash parentHash) {
        this.number = number;
        this.parentHash = parentHash == null ? emptyHash : parentHash;
    }

    public long getNumber() {
        return this.number;
    }

    public Hash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    public Hash getParentHash() {
        return this.parentHash;
    }

    private Hash calculateHash() {
        try {
            return new Hash(HashUtils.sha3(BlockHeaderEncoder.encode(this)));
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 22/08/2019.
 */
public class TransactionHash extends Hash {
    public TransactionHash(byte[] bytes) {
        super(bytes);
    }

    // TODO consider to remove this constructor
    public TransactionHash(Hash hash) {
        this(hash.getBytes());
    }
}

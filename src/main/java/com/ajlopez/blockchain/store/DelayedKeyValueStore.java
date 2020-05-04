package com.ajlopez.blockchain.store;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class DelayedKeyValueStore implements KeyValueStore {
    private final KeyValueStoreType storeType;
    private final KeyValueResolver keyValueResolver;

    public DelayedKeyValueStore(KeyValueStoreType storeType, KeyValueResolver keyValueResolver) {
        this.storeType = storeType;
        this.keyValueResolver = keyValueResolver;
    }

    @Override
    public void setValue(byte[] key, byte[] value) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getValue(byte[] key) throws IOException {
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        this.keyValueResolver.resolve(this.storeType, key, future);

        // TODO improve timeout
        try {
            return future.get(5, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}

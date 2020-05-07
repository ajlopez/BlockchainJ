package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.net.messages.GetStoredValueMessage;
import com.ajlopez.blockchain.processors.SendProcessor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class RemoteKeyValueStore implements KeyValueStore {
    private final KeyValueStoreType storeType;
    private final SendProcessor sendProcessor;
    private final KeyValueResolver keyValueResolver;

    public RemoteKeyValueStore(KeyValueStoreType storeType, SendProcessor sendProcessor, KeyValueResolver keyValueResolver) {
        this.storeType = storeType;
        this.sendProcessor = sendProcessor;
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

        GetStoredValueMessage getStoredValueMessage = new GetStoredValueMessage(this.storeType, key);
        this.sendProcessor.postMessage(getStoredValueMessage);

        // TODO improve timeout
        try {
            return future.get(5, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}

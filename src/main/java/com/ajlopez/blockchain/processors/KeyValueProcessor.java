package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.store.KeyValueResolver;
import com.ajlopez.blockchain.store.KeyValueStoreType;
import com.ajlopez.blockchain.utils.ByteArrayWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by ajlopez on 05/05/2020.
 */
public class KeyValueProcessor implements KeyValueResolver {
    private final Map<KeyValueStoreType, Map<ByteArrayWrapper, List<CompletableFuture<byte[]>>>> toResolve = new HashMap<>();
    private final Object lock = new Object();

    public void resolving(KeyValueStoreType storeType, byte[] key, byte[] value) {
        synchronized (this.lock) {
            if (!this.toResolve.containsKey(storeType))
                return;

            ByteArrayWrapper wrappedKey = new ByteArrayWrapper(key);

            if (!this.toResolve.get(storeType).containsKey(wrappedKey))
                return;

            for (CompletableFuture future: this.toResolve.get(storeType).get(wrappedKey))
                future.complete(value);
        }
    }

    @Override
    public void resolve(KeyValueStoreType storeType, byte[] key, CompletableFuture<byte[]> future) {
        synchronized (this.lock) {
            if (!this.toResolve.containsKey(storeType))
                this.toResolve.put(storeType, new HashMap<>());

            ByteArrayWrapper wrappedKey = new ByteArrayWrapper(key);

            if (!this.toResolve.get(storeType).containsKey(wrappedKey))
                this.toResolve.get(storeType).put(wrappedKey, new ArrayList<>());

            this.toResolve.get(storeType).get(wrappedKey).add(future);
        }
    }
}

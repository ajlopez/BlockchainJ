package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.KeyValueResolver;
import com.ajlopez.blockchain.store.KeyValueStoreType;
import com.ajlopez.blockchain.utils.ByteArrayWrapper;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by ajlopez on 05/05/2020.
 */
public class KeyValueProcessor implements KeyValueResolver {
    private final Map<KeyValueStoreType, Map<ByteArrayWrapper, List<CompletableFuture<byte[]>>>> toResolve = new HashMap<>();
    private final Object lock = new Object();

    public void resolving(KeyValueStoreType storeType, byte[] key, byte[] value) {
        if (isKeyHashValueStore(storeType))
            validateKeyHashValue(key, value);

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

    private static boolean isKeyHashValueStore(KeyValueStoreType keyValueStoreType) {
        return keyValueStoreType == KeyValueStoreType.ACCOUNTS || keyValueStoreType == KeyValueStoreType.STORAGE || keyValueStoreType == KeyValueStoreType.CODES;
    }

    private static void validateKeyHashValue(byte[] key, byte[] value) {
        Hash hash = HashUtils.calculateHash(value);

        if (!Arrays.equals(hash.getBytes(), key))
            throw new IllegalArgumentException("Invalid value for key");
    }
}

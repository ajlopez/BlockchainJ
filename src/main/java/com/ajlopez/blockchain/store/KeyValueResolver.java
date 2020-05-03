package com.ajlopez.blockchain.store;

import java.util.concurrent.CompletableFuture;

/**
 * Created by ajlopez on 03/05/2020.
 */
public interface KeyValueResolver {
    void resolve(StoreType storeType, byte[] key, CompletableFuture<byte[]> future);
}

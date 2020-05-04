package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class DelayedKeyValueStoreTest {
    @Test
    public void getValue() throws IOException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueResolver keyValueResolver = new KeyValueResolver() {
            @Override
            public void resolve(KeyValueStoreType storeType, byte[] key, CompletableFuture<byte[]> future) {
                new Thread(() -> { future.complete(value); }).start();
            }
        };

        DelayedKeyValueStore delayedKeyValueStore = new DelayedKeyValueStore(KeyValueStoreType.BLOCKS, keyValueResolver);

        byte[] result = delayedKeyValueStore.getValue(key);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);
    }
}

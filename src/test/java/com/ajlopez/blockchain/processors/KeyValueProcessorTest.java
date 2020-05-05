package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.store.DelayedKeyValueStore;
import com.ajlopez.blockchain.store.KeyValueResolver;
import com.ajlopez.blockchain.store.KeyValueStoreType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by ajlopez on 05/05/2020.
 */
public class KeyValueProcessorTest {
    @Test
    public void resolveValue() throws IOException, ExecutionException, InterruptedException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        keyValueProcessor.resolve(KeyValueStoreType.BLOCKS, key, future);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            keyValueProcessor.resolving(KeyValueStoreType.BLOCKS, key, value);
        }).start();

        byte[] result = future.get();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void resolveValueTwice() throws IOException, ExecutionException, InterruptedException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        CompletableFuture<byte[]> future2 = new CompletableFuture<>();

        keyValueProcessor.resolve(KeyValueStoreType.BLOCKS, key, future);
        keyValueProcessor.resolve(KeyValueStoreType.BLOCKS, key, future2);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            keyValueProcessor.resolving(KeyValueStoreType.BLOCKS, key, value);
        }).start();

        byte[] result = future.get();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);

        byte[] result2 = future2.get();

        Assert.assertNotNull(result2);
        Assert.assertArrayEquals(value, result2);
    }

    @Test
    public void resolveUnexpectedKey() throws IOException, ExecutionException, InterruptedException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();

        keyValueProcessor.resolving(KeyValueStoreType.BLOCKS, key, value);
    }
}

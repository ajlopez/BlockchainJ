package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.store.KeyValueStoreType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by ajlopez on 05/05/2020.
 */
public class KeyValueProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void resolveValue() throws ExecutionException, InterruptedException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        keyValueProcessor.resolve(KeyValueStoreType.BLOCKS_INFORMATION, key, future);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            keyValueProcessor.resolving(KeyValueStoreType.BLOCKS_INFORMATION, key, value);
        }).start();

        byte[] result = future.get();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void resolveValueTwice() throws ExecutionException, InterruptedException {
        byte[] value = FactoryHelper.createRandomBytes(42);
        byte[] key = HashUtils.calculateHash(value).getBytes();

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        CompletableFuture<byte[]> future2 = new CompletableFuture<>();

        keyValueProcessor.resolve(KeyValueStoreType.ACCOUNTS, key, future);
        keyValueProcessor.resolve(KeyValueStoreType.ACCOUNTS, key, future2);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            keyValueProcessor.resolving(KeyValueStoreType.ACCOUNTS, key, value);
        }).start();

        byte[] result = future.get();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);

        byte[] result2 = future2.get();

        Assert.assertNotNull(result2);
        Assert.assertArrayEquals(value, result2);
    }

    @Test
    public void resolveValueUsingAKeyHash() throws ExecutionException, InterruptedException {
        byte[] value = FactoryHelper.createRandomBytes(42);
        byte[] key = HashUtils.calculateHash(value).getBytes();

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        keyValueProcessor.resolve(KeyValueStoreType.ACCOUNTS, key, future);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            keyValueProcessor.resolving(KeyValueStoreType.ACCOUNTS, key, value);
        }).start();

        byte[] result = future.get();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void resolveValueUsingAnInvalidKeyHash() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid value for key");
        keyValueProcessor.resolving(KeyValueStoreType.ACCOUNTS, key, value);
    }

    @Test
    public void resolveValueUsingAnInvalidKeyHashForBlock() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChain(2);
        Block block1 = blockChain.getBlockByNumber(1);
        Block block2 = blockChain.getBlockByNumber(2);

        byte[] key = block1.getHash().getBytes();
        byte[] value = BlockEncoder.encode(block2);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid value for key");
        keyValueProcessor.resolving(KeyValueStoreType.BLOCKS, key, value);
    }

    @Test
    public void resolveUnexpectedKey() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueProcessor keyValueProcessor = new KeyValueProcessor();

        keyValueProcessor.resolving(KeyValueStoreType.BLOCKS_INFORMATION, key, value);
    }
}

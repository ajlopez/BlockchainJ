package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 14/08/2019.
 */
public class StreamProcessorTest {
    @Test
    public void processStreamInOneChunk() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        InputStream stream = new ByteArrayInputStream(data);
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        StreamProcessor streamProcessor = new StreamProcessor(chunkStore);

        streamProcessor.processStream(stream, 42);

        Hash expectedHash = HashUtils.calculateHash(data);

        Chunk expectedChunk = chunkStore.getChunk(expectedHash);

        Assert.assertNotNull(expectedChunk);
        Assert.assertArrayEquals(data, expectedChunk.getData());
    }

    @Test
    public void processStreamInTwoChunks() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(128);
        byte[] data1 = new byte[64];
        byte[] data2 = new byte[64];

        System.arraycopy(data, 0, data1, 0, 64);
        System.arraycopy(data, 64, data2, 0, 64);

        InputStream stream = new ByteArrayInputStream(data);
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        StreamProcessor streamProcessor = new StreamProcessor(chunkStore);

        streamProcessor.processStream(stream, 64);

        Hash expectedHash1 = HashUtils.calculateHash(data1);

        Chunk expectedChunk1 = chunkStore.getChunk(expectedHash1);

        Assert.assertNotNull(expectedChunk1);
        Assert.assertArrayEquals(data1, expectedChunk1.getData());

        Hash expectedHash2 = HashUtils.calculateHash(data2);

        Chunk expectedChunk2 = chunkStore.getChunk(expectedHash2);

        Assert.assertNotNull(expectedChunk2);
        Assert.assertArrayEquals(data2, expectedChunk2.getData());

        byte[] data3 = new byte[64];
        System.arraycopy(expectedHash1.getBytes(), 0, data3, 0, 32);
        System.arraycopy(expectedHash2.getBytes(), 0, data3, 32, 32);

        Hash expectedHash3 = HashUtils.calculateHash(data3);

        Chunk expectedChunk3 = chunkStore.getChunk(expectedHash3);

        Assert.assertNotNull(expectedChunk3);
        Assert.assertArrayEquals(data3, expectedChunk3.getData());
    }
}

package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 14/08/2019.
 */
public class StreamProcessorTest {
    @Test
    public void processStreamInOneChunk() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        InputStream stream = new ByteInputStream(data, data.length);
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        StreamProcessor streamProcessor = new StreamProcessor(chunkStore);

        streamProcessor.processStream(stream, 42);

        Hash expectedHash = HashUtils.calculateHash(data);

        Chunk expectedChunk = chunkStore.getChunk(expectedHash);

        Assert.assertNotNull(expectedChunk);
        Assert.assertArrayEquals(data, expectedChunk.getData());
    }
}

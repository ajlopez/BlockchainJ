package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 13/08/2019.
 */
public class ChunkStoreTest {
    @Test
    public void getUnknownChunk() throws IOException {
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        Assert.assertNull(chunkStore.getChunk(FactoryHelper.createRandomHash()));
    }

    @Test
    public void setAndGetChunk() throws IOException {
        Chunk chunk = new Chunk(FactoryHelper.createRandomBytes(42));
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        chunkStore.saveChunk(chunk);

        Chunk result = chunkStore.getChunk(chunk.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(chunk.getHash(), result.getHash());
        Assert.assertArrayEquals(chunk.getData(), result.getData());
    }
}

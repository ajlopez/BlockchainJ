package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 13/08/2019.
 */
public class ChunkStoreTest {
    @Test
    public void getUnknownChunk() {
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        Assert.assertNull(chunkStore.getChunk(FactoryHelper.createRandomHash()));
    }

    @Test
    public void setAndGetChunk() {
        Chunk chunk = new Chunk(FactoryHelper.createRandomBytes(42));
        ChunkStore chunkStore = new ChunkStore(new HashMapStore());

        chunkStore.saveChunk(chunk);

        Chunk result = chunkStore.getChunk(chunk.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(chunk.getHash(), result.getHash());
        Assert.assertArrayEquals(chunk.getData(), result.getData());
    }
}

package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by ajlopez on 11/08/2019.
 */
public class ChunkerTest {
    @Test
    public void noChunkFromEmptyStream() throws IOException {
        byte[] data = new byte[0];

        Chunker chunker = new Chunker(1024, new ByteArrayInputStream(data));

        Assert.assertNull(chunker.nextChunk());
    }

    @Test
    public void onlyOneChunk() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);

        Chunker chunker = new Chunker(1024, new ByteArrayInputStream(data));

        Chunk result = chunker.nextChunk();

        Assert.assertNotNull(result);
        Assert.assertEquals(data.length, result.getData().length);
        Assert.assertArrayEquals(data, result.getData());
        Assert.assertEquals(HashUtils.calculateHash(data), result.getHash());

        Assert.assertNull(chunker.nextChunk());
    }

    @Test
    public void twoChunks() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);

        Chunker chunker = new Chunker(40, new ByteArrayInputStream(data));

        Chunk result = chunker.nextChunk();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getData());
        Assert.assertEquals(40, result.getData().length);
        Assert.assertArrayEquals(ByteUtils.copyBytes(data, 40), result.getData());
        Assert.assertEquals(HashUtils.calculateHash(ByteUtils.copyBytes(data, 40)), result.getHash());

        Chunk result2 = chunker.nextChunk();

        Assert.assertNotNull(result2);
        Assert.assertNotNull(result2.getData());
        Assert.assertEquals(2, result2.getData().length);
        Assert.assertEquals(data[40], result2.getData()[0]);
        Assert.assertEquals(data[41], result2.getData()[1]);
    }
}

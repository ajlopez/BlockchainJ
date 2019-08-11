package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 11/08/2019.
 */
public class ChunkerTest {
    @Test
    public void noChunkFromEmptyStream() throws IOException {
        byte[] data = new byte[0];

        Chunker chunker = new Chunker(1024, new ByteInputStream(data, data.length));

        Assert.assertNull(chunker.nextChunk());
    }

    @Test
    public void onlyOneChunk() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);

        Chunker chunker = new Chunker(1024, new ByteInputStream(data, data.length));

        byte[] result = chunker.nextChunk();

        Assert.assertNotNull(result);
        Assert.assertEquals(data.length, result.length);
        Assert.assertArrayEquals(data, result);

        Assert.assertNull(chunker.nextChunk());
    }

    @Test
    public void twoChunks() throws IOException {
        byte[] data = FactoryHelper.createRandomBytes(42);

        Chunker chunker = new Chunker(40, new ByteInputStream(data, data.length));

        byte[] result = chunker.nextChunk();

        Assert.assertNotNull(result);
        Assert.assertEquals(40, result.length);
        Assert.assertArrayEquals(ByteUtils.copyBytes(data, 40), result);

        byte[] result2 = chunker.nextChunk();

        Assert.assertNotNull(result2);
        Assert.assertEquals(2, result2.length);
        Assert.assertEquals(data[40], result2[0]);
        Assert.assertEquals(data[41], result2[1]);
    }
}

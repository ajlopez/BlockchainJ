package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Hash;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by ajlopez on 10/10/2017.
 */
public class BlockEncoderTest {
    @Test
    public void encodeDecodeSimpleBlock() {
        Hash parentHash = new Hash();
        Block block = new Block(42, parentHash);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
    }

    @Test
    public void encodeTwoBlocks() {
        Hash parentHash = new Hash();
        Block block1 = new Block(42, parentHash);
        Block block2 = new Block(0, null);

        byte[] encoded1 = BlockEncoder.encode(block1);
        byte[] encoded2 = BlockEncoder.encode(block2);

        Assert.assertNotNull(encoded1);
        Assert.assertNotNull(encoded2);

        Assert.assertFalse(Arrays.equals(encoded1, encoded2));
    }
}

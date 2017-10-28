package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Hash;
import org.junit.Assert;
import org.junit.Test;

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
}

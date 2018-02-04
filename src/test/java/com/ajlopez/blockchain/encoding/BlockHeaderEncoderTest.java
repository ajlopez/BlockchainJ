package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by usuario on 23/09/2017.
 */
public class BlockHeaderEncoderTest {
    @Test
    public void encodeDecodeBlockHeader() {
        Hash hash = HashUtilsTest.generateRandomHash();
        Hash transactionsHash = HashUtilsTest.generateRandomHash();
        BlockHeader header = new BlockHeader(42, hash, transactionsHash);

        byte[] encoded = BlockHeaderEncoder.encode(header);

        Assert.assertNotNull(encoded);

        BlockHeader result = BlockHeaderEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(hash, result.getParentHash());
        Assert.assertEquals(header.getHash(), result.getHash());
        Assert.assertEquals(header.getTransactionsHash(), result.getTransactionsHash());
    }
}

package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 23/01/2021.
 */
public class BlockBuilderTest {
    @Test
    public void createBlockWithNumber() {
        BlockBuilder blockBuilder = new BlockBuilder();

        Block result = blockBuilder.number(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
    }

    @Test
    public void createBlockWithParent() {
        Block parent = new BlockBuilder().number(41).build();

        BlockBuilder blockBuilder = new BlockBuilder();

        Block result = blockBuilder.parent(parent).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(parent.getHash(), result.getParentHash());
    }

    @Test
    public void createBlockWithParentHash() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();

        BlockBuilder blockBuilder = new BlockBuilder();

        Block result = blockBuilder.parentHash(parentHash).number(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(parentHash, result.getParentHash());
    }

    @Test
    public void createBlockWithUncles() {
        List<BlockHeader> uncles = new ArrayList<>();

        BlockHeader uncle1a = new BlockBuilder().number(1).build().getHeader();
        BlockHeader uncle1b = new BlockBuilder().number(1).build().getHeader();

        uncles.add(uncle1a);
        uncles.add(uncle1b);

        BlockBuilder blockBuilder = new BlockBuilder();

        Block result = blockBuilder.number(2).uncles(uncles).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getNumber());

        Assert.assertNotNull(result.getUncles());
        Assert.assertEquals(2, result.getUncles().size());
        Assert.assertTrue(result.getUncles().contains(uncle1a));
        Assert.assertTrue(result.getUncles().contains(uncle1b));
    }
}
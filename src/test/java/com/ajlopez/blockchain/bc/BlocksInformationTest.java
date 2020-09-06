package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopez on 11/03/2020.
 */
public class BlocksInformationTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void noBlockInChain() {
        BlocksInformation blocksInformation = new BlocksInformation();

        Assert.assertNull(blocksInformation.getBlockOnChain());
        Assert.assertEquals(-1, blocksInformation.getBlockOnChainPosition());
        Assert.assertTrue(blocksInformation.getBlockInformationList().isEmpty());
    }

    @Test
    public void addBlockNotOnChain() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);

        BlocksInformation blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(blockHash, totalDifficulty);

        BlockInformation blockInformation = blocksInformation.getBlockInformation(blockHash);

        Assert.assertNotNull(blockInformation);
        Assert.assertEquals(blockHash, blockInformation.getBlockHash());
        Assert.assertEquals(totalDifficulty, blockInformation.getTotalDifficulty());

        Assert.assertNull(blocksInformation.getBlockOnChain());
        Assert.assertEquals(-1, blocksInformation.getBlockOnChainPosition());
        Assert.assertEquals(1, blocksInformation.getBlockInformationList().size());
    }

    @Test
    public void addBlockOnChain() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);

        BlocksInformation blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(blockHash, totalDifficulty);
        blocksInformation.setBlockOnChain(blockHash);

        BlockInformation blockInformation = blocksInformation.getBlockInformation(blockHash);

        Assert.assertNotNull(blockInformation);
        Assert.assertEquals(blockHash, blockInformation.getBlockHash());
        Assert.assertEquals(totalDifficulty, blockInformation.getTotalDifficulty());

        Assert.assertSame(blockInformation, blocksInformation.getBlockOnChain());
        Assert.assertEquals(1, blocksInformation.getBlockInformationList().size());
    }

    @Test
    public void setUnknownBlockOnChain() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();

        BlocksInformation blocksInformation = new BlocksInformation();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Block not found");
        blocksInformation.setBlockOnChain(blockHash);
    }

    @Test
    public void getUnknownBlockInformation() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();

        BlocksInformation blocksInformation = new BlocksInformation();

        Assert.assertNull(blocksInformation.getBlockInformation(blockHash));
    }

    @Test
    public void addBlockOnChainAndThenOffChain() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);

        BlocksInformation blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(blockHash, totalDifficulty);
        blocksInformation.setBlockOnChain(blockHash);
        blocksInformation.setBlockOffChain(blockHash);

        BlockInformation blockInformation = blocksInformation.getBlockInformation(blockHash);

        Assert.assertNotNull(blockInformation);
        Assert.assertEquals(blockHash, blockInformation.getBlockHash());
        Assert.assertEquals(totalDifficulty, blockInformation.getTotalDifficulty());

        Assert.assertNull(blocksInformation.getBlockOnChain());
        Assert.assertEquals(1, blocksInformation.getBlockInformationList().size());
    }
}

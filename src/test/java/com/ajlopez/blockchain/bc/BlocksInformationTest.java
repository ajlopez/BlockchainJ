package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 11/03/2020.
 */
public class BlocksInformationTest {
    @Test
    public void noBlockInChain() {
        BlocksInformation blocksInformation = new BlocksInformation();

        Assert.assertNull(blocksInformation.getBlockOnChain());
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
    }
}

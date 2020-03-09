package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 26/08/2017.
 */
public class BlockInformationTest {
    @Test
    public void simpleCreate() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);

        BlockInformation blockInformation = new BlockInformation(blockHash, totalDifficulty);

        Assert.assertEquals(blockHash, blockInformation.getBlockHash());
        Assert.assertEquals(totalDifficulty, blockInformation.getTotalDifficulty());
    }
}

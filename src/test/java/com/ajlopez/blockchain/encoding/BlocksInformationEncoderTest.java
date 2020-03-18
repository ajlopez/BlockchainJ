package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.bc.BlocksInformation;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 18/03/2020.
 */
public class BlocksInformationEncoderTest {
    @Test
    public void encodeDecode() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);
        BlockHash blockHash2 = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty2 = Difficulty.fromUnsignedLong(100);

        BlocksInformation blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(blockHash, totalDifficulty);
        blocksInformation.addBlockInformation(blockHash2, totalDifficulty2);

        byte[] encoded = BlocksInformationEncoder.encode(blocksInformation);

        Assert.assertNotNull(encoded);

        BlocksInformation result = BlocksInformationEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(blocksInformation.getBlockOnChainPosition(), result.getBlockOnChainPosition());
        Assert.assertEquals(blocksInformation.getBlockInformationList().size(), result.getBlockInformationList().size());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(0).getBlockHash(), result.getBlockInformationList().get(0).getBlockHash());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(0).getTotalDifficulty(), result.getBlockInformationList().get(0).getTotalDifficulty());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(1).getBlockHash(), result.getBlockInformationList().get(1).getBlockHash());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(1).getTotalDifficulty(), result.getBlockInformationList().get(1).getTotalDifficulty());
    }
}

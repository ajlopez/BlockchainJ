package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.bc.BlockInformation;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 17/03/2020.
 */
public class BlockInformationEncoderTest {
    @Test
    public void encodeAndDecode() {
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);

        BlockInformation blockInformation = new BlockInformation(blockHash, totalDifficulty);

        byte[] encoded = BlockInformationEncoder.encode(blockInformation);

        Assert.assertNotNull(encoded);

        BlockInformation result = BlockInformationEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(blockHash, result.getBlockHash());
        Assert.assertEquals(totalDifficulty, result.getTotalDifficulty());
    }
}

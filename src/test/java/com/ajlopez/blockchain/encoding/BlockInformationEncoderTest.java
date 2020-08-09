package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.bc.BlockInformation;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopez on 17/03/2020.
 */
public class BlockInformationEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

    @Test
    public void decodeInvalidEncodedAccount() {
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(RLP.encode(bytes));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid block information encoding");
        BlockInformationEncoder.decode(encoded);
    }
}

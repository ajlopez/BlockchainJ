package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StatusEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeStatus() {
        PeerId nodeid = FactoryHelper.createRandomPeerId();
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Status status = new Status(nodeid, 2, 3, blockHash, Difficulty.TEN);

        byte[] bytes = StatusEncoder.encode(status);

        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(0, bytes.length);

        Status result = StatusEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(nodeid, result.getPeerId());
        Assert.assertEquals(2, result.getNetworkNumber());
        Assert.assertEquals(3, result.getBestBlockNumber());
        Assert.assertEquals(blockHash, result.getBestBlockHash());
        Assert.assertEquals(Difficulty.TEN, result.getBestTotalDifficulty());
    }

    @Test
    public void decodeInvalidEncodedStatus() {
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(RLP.encode(bytes));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid status encoding");
        StatusEncoder.decode(encoded);
    }
}

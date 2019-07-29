package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 05/02/2018.
 */
public class StatusMessageTest {
    @Test
    public void createWithData() {
        PeerId nodeid = FactoryHelper.createRandomPeerId();
        BlockHash blockHash = FactoryHelper.createRandomBlockHash();

        StatusMessage message = new StatusMessage(new Status(nodeid, 2, 3, blockHash));

        Assert.assertEquals(nodeid, message.getStatus().getPeerId());
        Assert.assertEquals(2, message.getStatus().getNetworkNumber());
        Assert.assertEquals(blockHash, message.getStatus().getBestBlockHash());
        Assert.assertEquals(3, message.getStatus().getBestBlockNumber());
    }
}

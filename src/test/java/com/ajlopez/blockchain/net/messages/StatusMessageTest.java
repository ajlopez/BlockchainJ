package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 05/02/2018.
 */
public class StatusMessageTest {
    @Test
    public void createWithData() {
        Hash nodeid = HashUtilsTest.generateRandomHash();
        StatusMessage message = new StatusMessage(nodeid, 2, 3);

        Assert.assertEquals(nodeid, message.getNodeId());
        Assert.assertEquals(2, message.getNetworkNumber());
        Assert.assertEquals(3, message.getBestBlockNumber());
    }
}

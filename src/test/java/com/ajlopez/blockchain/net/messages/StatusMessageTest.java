package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 05/02/2018.
 */
public class StatusMessageTest {
    @Test
    public void createWithData() {
        PeerId nodeid = HashUtilsTest.generateRandomPeerId();
        StatusMessage message = new StatusMessage(new Status(nodeid, 2, 3));

        Assert.assertEquals(nodeid, message.getStatus().getNodeId());
        Assert.assertEquals(2, message.getStatus().getNetworkNumber());
        Assert.assertEquals(3, message.getStatus().getBestBlockNumber());
    }
}

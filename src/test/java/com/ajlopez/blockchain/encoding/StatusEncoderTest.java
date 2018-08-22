package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

public class StatusEncoderTest {
    @Test
    public void encodeDecodeStatus() {
        Hash nodeid = HashUtilsTest.generateRandomHash();
        Status status = new Status(nodeid, 2, 3);

        byte[] bytes = StatusEncoder.encode(status);

        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(0, bytes.length);

        Status result = StatusEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(nodeid, result.getNodeId());
        Assert.assertEquals(2, result.getNetworkNumber());
        Assert.assertEquals(3, result.getBestBlockNumber());
    }
}

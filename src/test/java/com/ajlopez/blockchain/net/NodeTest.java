package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class NodeTest {
    @Test
    public void createWithHash() {
        Hash hash = HashUtilsTest.generateRandomHash();

        Node node = new Node(hash);

        Assert.assertEquals(hash, node.getHash());
    }
}

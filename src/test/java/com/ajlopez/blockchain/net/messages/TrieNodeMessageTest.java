package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 07/06/2019.
 */
public class TrieNodeMessageTest {
    @Test
    public void createWithData() {
        Hash topHash = FactoryHelper.createRandomHash();
        byte[] trieNode = FactoryHelper.createRandomBytes(42);

        TrieNodeMessage message = new TrieNodeMessage(topHash, TrieType.ACCOUNT, trieNode);

        Assert.assertEquals(MessageType.TRIE_NODE, message.getMessageType());
        Assert.assertEquals(topHash, message.getTopHash());
        Assert.assertEquals(TrieType.ACCOUNT, message.getTrieType());
        Assert.assertArrayEquals(trieNode, message.getTrieNode());
        Assert.assertFalse(message.isPriorityMessage());
    }
}

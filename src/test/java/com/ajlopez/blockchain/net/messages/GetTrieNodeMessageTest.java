package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 21/06/2019.
 */
public class GetTrieNodeMessageTest {
    @Test
    public void createMessage() {
        TrieType trieType = TrieType.ACCOUNT;
        Hash trieHash = FactoryHelper.createRandomHash();

        GetTrieNodeMessage message = new GetTrieNodeMessage(trieType, trieHash);

        Assert.assertEquals(trieType, message.getTrieType());
        Assert.assertEquals(trieHash, message.getTrieHash());
    }
}

package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 20/01/2018.
 */
public class TransactionMessageTest {
    @Test
    public void createWithTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        TransactionMessage message = new TransactionMessage(tx);

        Assert.assertEquals(MessageType.TRANSACTION, message.getMessageType());
        Assert.assertArrayEquals(TransactionEncoder.encode(tx), message.getPayload());
        Assert.assertTrue(message.isPriorityMessage());
    }
}

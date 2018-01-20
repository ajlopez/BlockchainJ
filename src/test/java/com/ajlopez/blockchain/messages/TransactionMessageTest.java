package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.Address;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 20/01/2018.
 */
public class TransactionMessageTest {
    @Test
    public void createWithTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42);

        TransactionMessage message = new TransactionMessage(tx);

        Assert.assertEquals(MessageType.TRANSACTION, message.getMessageType());
        Assert.assertArrayEquals(TransactionEncoder.encode(tx), message.getPayload());
    }
}

package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 30/09/2017.
 */
public class TransactionTest {
    @Test
    public void createTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(value, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());
        Assert.assertNotEquals(Hash.emptyHash(), tx.getHash());
    }
}

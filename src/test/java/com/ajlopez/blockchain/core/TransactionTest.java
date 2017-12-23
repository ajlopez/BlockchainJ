package com.ajlopez.blockchain.core;

import com.sun.javaws.exceptions.InvalidArgumentException;
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

    @Test
    public void createTransactionWithNullValue() {
        Address sender = new Address();
        Address receiver = new Address();

        Transaction tx = new Transaction(sender, receiver, null, 42);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(BigInteger.ZERO, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());
        Assert.assertNotEquals(Hash.emptyHash(), tx.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNegativeNonce() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        new Transaction(sender, receiver, value, -1);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoSender() {
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        new Transaction(null, receiver, value, 42);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoReceiver() {
        Address sender = new Address();
        BigInteger value = BigInteger.ONE;

        new Transaction(sender, null, value, 42);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNegativeValue() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE.negate();

        new Transaction(sender, receiver, value, 42);
    }
}

package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 30/09/2017.
 */
public class TransactionTest {
    @Test
    public void createTransaction() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42, null, 6000000, BigInteger.ZERO);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(value, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(BigInteger.ZERO, tx.getGasPrice());
    }

    @Test
    public void createTransactionWithNullValue() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        Transaction tx = new Transaction(sender, receiver, null, 42, null, 6000000, BigInteger.ZERO);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(BigInteger.ZERO, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(BigInteger.ZERO, tx.getGasPrice());
    }

    @Test
    public void createTransactionWithNullGasPrice() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        Transaction tx = new Transaction(sender, receiver, null, 42, null, 6000000, null);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(BigInteger.ZERO, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(BigInteger.ZERO, tx.getGasPrice());
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNegativeNonce() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        BigInteger value = BigInteger.ONE;

        new Transaction(sender, receiver, value, -1, null, 6000000, BigInteger.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoSender() {
        Address receiver = FactoryHelper.createRandomAddress();
        BigInteger value = BigInteger.ONE;

        new Transaction(null, receiver, value, 42, null, 6000000, BigInteger.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoReceiver() {
        Address sender = FactoryHelper.createRandomAddress();
        BigInteger value = BigInteger.ONE;

        new Transaction(sender, null, value, 42, null, 6000000, BigInteger.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNegativeValue() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        BigInteger value = BigInteger.ONE.negate();

        new Transaction(sender, receiver, value, 42, null, 6000000, BigInteger.ZERO);
    }
}

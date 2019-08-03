package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 30/09/2017.
 */
public class TransactionTest {
    @Test
    public void createTransaction() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42, null, 6000000, Coin.ZERO);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(value, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(Coin.ZERO, tx.getGasPrice());
    }

    @Test
    public void createTransactionWithNullValue() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        Transaction tx = new Transaction(sender, receiver, null, 42, null, 6000000, Coin.ZERO);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(Coin.ZERO, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(Coin.ZERO, tx.getGasPrice());
    }

    @Test
    public void createTransactionWithNullGasPrice() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        Transaction tx = new Transaction(sender, receiver, null, 42, null, 6000000, null);

        Assert.assertEquals(sender, tx.getSender());
        Assert.assertEquals(receiver, tx.getReceiver());
        Assert.assertEquals(Coin.ZERO, tx.getValue());
        Assert.assertEquals(42, tx.getNonce());

        Assert.assertNotNull(tx.getHash());

        Assert.assertNull(tx.getData());

        Assert.assertEquals(6000000, tx.getGas());
        Assert.assertEquals(Coin.ZERO, tx.getGasPrice());
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNegativeNonce() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        new Transaction(sender, receiver, value, -1, null, 6000000, Coin.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoSender() {
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        new Transaction(null, receiver, value, 42, null, 6000000, Coin.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void createTransactionWithNoReceiver() {
        Address sender = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        new Transaction(sender, null, value, 42, null, 6000000, Coin.ZERO);
    }
}

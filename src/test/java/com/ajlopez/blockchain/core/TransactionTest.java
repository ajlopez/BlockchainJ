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

    @Test
    public void equalTransaction() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        byte[] data = FactoryHelper.createRandomBytes(128);

        Transaction tx = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);
        Transaction tx2 = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);
        Transaction tx3 = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ONE);

        Assert.assertTrue(tx.equals(tx));
        Assert.assertTrue(tx.equals(tx2));
        Assert.assertTrue(tx2.equals(tx));
        Assert.assertEquals(tx.hashCode(), tx2.hashCode());

        Assert.assertFalse(tx.equals(tx3));
        Assert.assertFalse(tx2.equals(tx3));
        Assert.assertFalse(tx3.equals(tx));
        Assert.assertFalse(tx3.equals(tx2));
        Assert.assertFalse(tx.equals(null));
        Assert.assertFalse(tx.equals("foo"));
    }
    
    @Test
    public void withNonce() {
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, to, value, nonce, data, gas, gasPrice);

        Transaction result = transaction.withNonce(18);

        Assert.assertNotNull(result);
        Assert.assertEquals(18, result.getNonce());

        Transaction result2 = result.withNonce(17);

        Assert.assertNotNull(result2);
        Assert.assertEquals(17, result2.getNonce());
        Assert.assertEquals(transaction, result2);
    }
}

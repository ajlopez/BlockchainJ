package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopezo on 04/10/2017.
 */
public class TransactionEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        byte[] encoded = TransactionEncoder.encode(tx);

        Assert.assertNotNull(encoded);

        Transaction result = TransactionEncoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(tx.getSender(), result.getSender());
        Assert.assertEquals(tx.getReceiver(), result.getReceiver());
        Assert.assertEquals(tx.getValue(), result.getValue());
        Assert.assertEquals(tx.getNonce(), result.getNonce());
        Assert.assertNull(result.getData());

        Assert.assertNotNull(result.getHash());
        Assert.assertEquals(tx.getHash(), result.getHash());

        Assert.assertEquals(tx.getGas(), result.getGas());
        Assert.assertEquals(tx.getGasPrice(), result.getGasPrice());
    }

    @Test
    public void encodeDecodeTransactionWithNoReceiver() {
        Transaction tx = FactoryHelper.createTransaction(42);

        byte[] encoded = TransactionEncoder.encode(tx);

        Assert.assertNotNull(encoded);

        Transaction result = TransactionEncoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(tx.getSender(), result.getSender());
        Assert.assertEquals(tx.getReceiver(), result.getReceiver());
        Assert.assertEquals(tx.getValue(), result.getValue());
        Assert.assertEquals(tx.getNonce(), result.getNonce());
        Assert.assertNull(result.getData());

        Assert.assertNotNull(result.getHash());
        Assert.assertEquals(tx.getHash(), result.getHash());

        Assert.assertEquals(tx.getGas(), result.getGas());
        Assert.assertEquals(tx.getGasPrice(), result.getGasPrice());
    }

    @Test
    public void encodeDecodeTransactionWithGasAndGasPrice() {
        Address sender = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        Transaction transaction = new Transaction(sender, null, value, 42, null, 6000000, Coin.ZERO);

        byte[] encoded = TransactionEncoder.encode(transaction);

        Assert.assertNotNull(encoded);

        Transaction result = TransactionEncoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertNull(result.getReceiver());
        Assert.assertEquals(transaction.getValue(), result.getValue());
        Assert.assertEquals(transaction.getNonce(), result.getNonce());
        Assert.assertNull(result.getData());

        Assert.assertNotNull(result.getHash());
        Assert.assertEquals(transaction.getHash(), result.getHash());

        Assert.assertEquals(transaction.getGas(), result.getGas());
        Assert.assertEquals(transaction.getGasPrice(), result.getGasPrice());
    }

    @Test
    public void encodeDecodeTransactionWithData() {
        byte[] data = FactoryHelper.createRandomBytes(42);
        Transaction tx = FactoryHelper.createTransaction(42, 0, data);

        byte[] encoded = TransactionEncoder.encode(tx);

        Assert.assertNotNull(encoded);

        Transaction result = TransactionEncoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(tx.getSender(), result.getSender());
        Assert.assertEquals(tx.getReceiver(), result.getReceiver());
        Assert.assertEquals(tx.getValue(), result.getValue());
        Assert.assertEquals(tx.getNonce(), result.getNonce());
        Assert.assertNotNull(result.getData());
        Assert.assertArrayEquals(data, result.getData());

        Assert.assertNotNull(result.getHash());
        Assert.assertEquals(tx.getHash(), result.getHash());
    }

    @Test
    public void decodeInvalidEncodedTransaction() {
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(RLP.encode(bytes));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid transaction encoding");
        TransactionEncoder.decode(encoded);
    }
}

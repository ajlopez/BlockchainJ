package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopezo on 04/10/2017.
 */
public class TransactionEncoderTest {
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
    public void encodeDecodeTransactionWithGasAndGasPrice() {
        Transaction tx = FactoryHelper.createTransaction(42, 0, null, 100000, 1);

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
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.core.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopezo on 04/10/2017.
 */
public class TransactionEncoderTest {
    @Test
    public void encodeDecodeTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42);

        byte[] encoded = TransactionEncoder.encode(tx);

        Assert.assertNotNull(encoded);

        Transaction result = TransactionEncoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(tx.getSender(), result.getSender());
        Assert.assertEquals(tx.getReceiver(), result.getReceiver());
        Assert.assertEquals(tx.getValue(), result.getValue());
        Assert.assertEquals(tx.getNonce(), result.getNonce());
        Assert.assertNotNull(result.getHash());
        Assert.assertEquals(tx.getHash(), result.getHash());
    }
}

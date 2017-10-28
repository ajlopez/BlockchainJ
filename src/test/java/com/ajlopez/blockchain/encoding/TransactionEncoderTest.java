package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Address;
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

        Transaction tx = new Transaction(sender, receiver, value);

        TransactionEncoder encoder = new TransactionEncoder();

        byte[] encoded = encoder.encode(tx);

        Assert.assertNotNull(encoded);

        Transaction result = encoder.decode(encoded);

        Assert.assertNotNull(result);

        Assert.assertEquals(tx.getSender(), result.getSender());
        Assert.assertEquals(tx.getReceiver(), result.getReceiver());
        Assert.assertEquals(tx.getValue(), result.getValue());
    }
}

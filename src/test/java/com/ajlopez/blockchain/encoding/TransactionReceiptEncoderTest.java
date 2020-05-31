package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.TransactionReceipt;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopez on 26/05/2020.
 */
public class TransactionReceiptEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeTransactionReceipt() {
        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true, null);

        byte[] encoded = TransactionReceiptEncoder.encode(transactionReceipt);

        Assert.assertNotNull(encoded);

        TransactionReceipt result = TransactionReceiptEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getGasUsed());
        Assert.assertTrue(result.getSuccess());
    }

    @Test
    public void decodeInvalidEncodedTransactionReceipt() {
        byte[] encoded = RLP.encodeList(RLP.encode(new byte[1]), RLP.encode(new byte[2]), RLP.encode(new byte[3]));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid transaction receipt encoding");
        TransactionReceiptEncoder.decode(encoded);
    }
}

package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 25/05/2020.
 */
public class TransactionReceiptTest {
    @Test
    public void simpleCreationTest() {
        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true);
        
        Assert.assertEquals(42, transactionReceipt.getGasUsed());
        Assert.assertTrue(transactionReceipt.getSuccess());
    }
}

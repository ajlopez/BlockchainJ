package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProviderTest {
    @Test
    public void getUnknownTransactionAsNull() {
        Hash hash = FactoryHelper.createRandomHash();
        String txid = hash.toString();

        TransactionsProvider transactionsProvider = new TransactionsProvider(null);

        Assert.assertNull(transactionsProvider.getTransaction(txid));
    }
}

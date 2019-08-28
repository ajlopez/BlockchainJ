package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 27/08/2019.
 */
public class TransactionsProcessorTest {
    @Test
    public void getUnknownTransaction() throws JsonRpcException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(FactoryHelper.createRandomHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionByHash", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.NULL, response.getResult().getType());
    }

    @Test
    public void getTransaction() throws JsonRpcException {
        TransactionPool transactionPool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        transactionPool.addTransaction(transaction);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(transaction.getHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionByHash", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());
    }
}

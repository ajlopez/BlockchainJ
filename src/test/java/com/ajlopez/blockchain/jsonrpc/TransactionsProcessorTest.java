package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 27/08/2019.
 */
public class TransactionsProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getUnknownTransaction() throws JsonRpcException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null);

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

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(transaction.getHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionByHash", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue oresult = (JsonObjectValue)response.getResult();

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("from"));
        Assert.assertTrue(oresult.hasProperty("to"));
        Assert.assertTrue(oresult.hasProperty("value"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("gas"));
        Assert.assertTrue(oresult.hasProperty("gasPrice"));

        Assert.assertEquals(transaction.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(transaction.getSender().toString(), oresult.getProperty("from").getValue());
        Assert.assertEquals(transaction.getReceiver().toString(), oresult.getProperty("to").getValue());
        Assert.assertEquals(transaction.getValue().toString(), oresult.getProperty("value").getValue());
        Assert.assertEquals(transaction.getNonce() + "", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(transaction.getGas() + "", oresult.getProperty("gas").getValue());
        Assert.assertEquals(transaction.getGasPrice().toString(), oresult.getProperty("gasPrice").getValue());
    }

    @Test
    public void sendTransaction() throws JsonRpcException {
        TransactionPool transactionPool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null);

        List<JsonValue> params = new ArrayList<>();
        params.add(TransactionJsonEncoder.encode(transaction));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_sendTransaction", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.STRING, response.getResult().getType());

        List<Transaction> transactions = transactionPool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
    }

    @Test
    public void unknownMethod() throws JsonRpcException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null);

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        transactionsProcessor.processRequest(request);
    }
}

package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoderTest;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.processors.TransactionProcessor;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
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
    public void getUnknownTransaction() throws JsonRpcException, IOException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null, null);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(FactoryHelper.createRandomHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionByHash", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.NULL, response.getResult().getType());
    }

    @Test
    public void getTransaction() throws JsonRpcException, IOException {
        TransactionPool transactionPool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        transactionPool.addTransaction(transaction);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null, null);

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
    public void sendTransaction() throws JsonRpcException, IOException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionPool);
        Transaction transaction = FactoryHelper.createTransaction(1000);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null, transactionProcessor);

        List<JsonValue> params = new ArrayList<>();
        params.add(TransactionJsonEncoder.encode(transaction, true, true));
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
    public void sendTransactionWithoutNonce() throws JsonRpcException, IOException {
        Stores stores = new MemoryStores();
        BlockChain blockchain = FactoryHelper.createBlockChain(stores,10, 1);
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(stores.getAccountTrieStore());
        Address sender = blockchain.getBlockByNumber(1).getTransactions().get(0).getSender();

        BlocksProvider blocksProvider = new BlocksProvider(blockchain);
        AccountsProvider accountsProvider = new AccountsProvider(blocksProvider, accountStoreProvider);

        TransactionPool transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionPool);
        Transaction transaction = FactoryHelper.createTransaction(1000, sender, 0);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, accountsProvider, transactionProcessor);

        JsonObjectValue jovalue = TransactionJsonEncoderTest.removeProperty((JsonObjectValue)TransactionJsonEncoder.encode(transaction, true, true), "nonce");

        List<JsonValue> params = new ArrayList<>();
        params.add(jovalue);
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_sendTransaction", params);

        JsonRpcResponse response = transactionsProcessor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(JsonValueType.STRING, response.getResult().getType());

        List<Transaction> transactions = transactionPool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(10, transactions.get(0).getNonce());
    }

    @Test
    public void sendTwoTransactionsWithoutNonce() throws JsonRpcException, IOException {
        Stores stores = new MemoryStores();
        BlockChain blockchain = FactoryHelper.createBlockChain(stores,10, 1);
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(stores.getAccountTrieStore());
        Address sender = blockchain.getBlockByNumber(1).getTransactions().get(0).getSender();

        BlocksProvider blocksProvider = new BlocksProvider(blockchain);
        AccountsProvider accountsProvider = new AccountsProvider(blocksProvider, accountStoreProvider);

        TransactionPool transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionPool);
        Transaction transaction1 = FactoryHelper.createTransaction(1000, sender, 0);
        Transaction transaction2 = FactoryHelper.createTransaction(1000, sender, 0);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, accountsProvider, transactionProcessor);

        JsonObjectValue jovalue1 = TransactionJsonEncoderTest.removeProperty((JsonObjectValue)TransactionJsonEncoder.encode(transaction1, true, true), "nonce");

        List<JsonValue> params1 = new ArrayList<>();
        params1.add(jovalue1);
        JsonRpcRequest request1 =  new JsonRpcRequest("1", "2.0", "eth_sendTransaction", params1);

        JsonRpcResponse response1 = transactionsProcessor.processRequest(request1);

        Assert.assertNotNull(response1);
        Assert.assertNotNull(response1.getResult());
        Assert.assertEquals(JsonValueType.STRING, response1.getResult().getType());

        JsonObjectValue jovalue2 = TransactionJsonEncoderTest.removeProperty((JsonObjectValue)TransactionJsonEncoder.encode(transaction2, true, true), "nonce");

        List<JsonValue> params2 = new ArrayList<>();
        params2.add(jovalue2);
        JsonRpcRequest request2 =  new JsonRpcRequest("1", "2.0", "eth_sendTransaction", params2);

        JsonRpcResponse response2 = transactionsProcessor.processRequest(request2);

        Assert.assertNotNull(response2);
        Assert.assertNotNull(response2.getResult());
        Assert.assertEquals(JsonValueType.STRING, response2.getResult().getType());

        List<Transaction> transactions = transactionPool.getTransactionsWithSenderFromNonce(sender, 10);

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(2, transactions.size());
        Assert.assertEquals(10, transactions.get(0).getNonce());
        Assert.assertEquals(11, transactions.get(1).getNonce());
    }

    @Test
    public void unknownMethod() throws JsonRpcException, IOException {
        TransactionPool transactionPool = new TransactionPool();
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null, null);

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        transactionsProcessor.processRequest(request);
    }
}

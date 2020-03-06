package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.jsonrpc.TransactionsProvider;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.processors.TransactionProcessor;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ajlopez on 26/02/2020.
 */
public class RpcRunnerTest {
    @Test
    public void simpleRequest() throws IOException {
        RpcRunner rpcRunner = new RpcRunner(6000, null, null, null, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("POST /\r\n");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);
    }

    @Test
    public void getBlockNumber() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChain(10);

        RpcRunner rpcRunner = new RpcRunner(6001, blockChain, null, null, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6001);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_blockNumber\", \"params\": [] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
            + reader.readLine() + "\r\n"
            + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": \"0x0a\" }", result);
    }

    @Test
    public void getNetworkVersion() throws IOException {
        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)42);

        RpcRunner rpcRunner = new RpcRunner(6002, null, null, null, null, networkConfiguration);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6002);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"net_version\", \"params\": [] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
                + reader.readLine() + "\r\n"
                + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": \"0x2a\" }", result);
    }

    @Test
    public void getUnknownTransactionByHash() throws IOException {
        Hash transactionHash = FactoryHelper.createRandomHash();
        String hash = transactionHash.toString();
        TransactionPool transactionPool = new TransactionPool();

        RpcRunner rpcRunner = new RpcRunner(6003, null, null, transactionPool, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6003);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_getTransactionByHash\", \"params\": [ \"" + hash + "\"] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
                + reader.readLine() + "\r\n"
                + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": null }", result);
    }

    @Test
    public void sendTransaction() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(new TrieStore(new HashMapStore()));
        Transaction transaction = FactoryHelper.createTransaction(1000);
        TransactionPool transactionPool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionPool);

        RpcRunner rpcRunner = new RpcRunner(6004, blockChain, accountStoreProvider, transactionPool, transactionProcessor, null);

        rpcRunner.start();

        JsonValue transactionJson = TransactionJsonEncoder.encode(transaction, false, false);

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_sendTransaction\", \"params\": [ " + transactionJson.toString() + " ] }";

        Socket socket = new Socket("127.0.0.1", 6004);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
                + reader.readLine() + "\r\n"
                + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": \"" + transaction.getHash().toString() + "\" }", result);

        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        Transaction processed = transactionsProvider.getTransaction(transaction.getHash().toString());

        Assert.assertNotNull(processed);
        Assert.assertEquals(transaction.getHash(), processed.getHash());
    }

    @Test
    public void getBlockByHash() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChain(10);

        RpcRunner rpcRunner = new RpcRunner(6005, blockChain, null, null, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6005);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        Block block = blockChain.getBlockByNumber(1);

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_getBlockByHash\", \"params\": [ \"" + block.getHash().toString() + "\" ] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        // TODO improve test
        Assert.assertEquals("HTTP/1.1 200 OK", result);
    }
}

package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.jsonrpc.*;
import com.ajlopez.blockchain.net.http.HttpServer;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.processors.TransactionProcessor;

/**
 * Created by ajlopez on 26/02/2020.
 */
public class RpcRunner {
    private final int port;
    private final HttpServer httpServer;

    public RpcRunner(int port, BlockChain blockChain, TransactionPool transactionPool, TransactionProcessor transactionProcessor, NetworkConfiguration networkConfiguration) {
        TopProcessor topProcessor = new TopProcessor();
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);
        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);
        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(transactionsProvider, null, transactionProcessor);
        NetworkProcessor networkProcessor = new NetworkProcessor(networkConfiguration);

        topProcessor.registerProcess("eth_blockNumber", blocksProcessor);
        topProcessor.registerProcess("eth_getBlockByNumber", blocksProcessor);
        topProcessor.registerProcess("eth_getBlockByHash", blocksProcessor);

        topProcessor.registerProcess("eth_getTransactionByHash", transactionsProcessor);
        topProcessor.registerProcess("eth_sendTransaction", transactionsProcessor);

        topProcessor.registerProcess("net_version", networkProcessor);

        this.port = port;
        this.httpServer = new HttpServer(this.port, topProcessor);
    }

    public void start() {
        System.out.println(String.format("Starting RPC server at port %d", this.port));
        this.httpServer.start();
    }

    public void stop() {
        this.httpServer.stop();

        System.out.println(String.format("Stopping RPC server at port %d", this.port));
    }
}

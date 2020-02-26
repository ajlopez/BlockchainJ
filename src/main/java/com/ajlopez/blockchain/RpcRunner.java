package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.jsonrpc.BlocksProcessor;
import com.ajlopez.blockchain.jsonrpc.NetworkProcessor;
import com.ajlopez.blockchain.jsonrpc.TopProcessor;
import com.ajlopez.blockchain.net.http.HttpServer;

/**
 * Created by ajlopez on 26/02/2020.
 */
public class RpcRunner {
    private final int port;
    private final HttpServer httpServer;

    public RpcRunner(int port, BlockChain blockChain, NetworkConfiguration networkConfiguration) {
        TopProcessor topProcessor = new TopProcessor();
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);
        NetworkProcessor networkProcessor = new NetworkProcessor(networkConfiguration);

        topProcessor.registerProcess("eth_blockNumber", blocksProcessor);
        topProcessor.registerProcess("eth_getBlockByNumber", blocksProcessor);
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

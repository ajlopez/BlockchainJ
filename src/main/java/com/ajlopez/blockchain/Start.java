package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.jsonrpc.BlocksProcessor;
import com.ajlopez.blockchain.jsonrpc.NetworkProcessor;
import com.ajlopez.blockchain.jsonrpc.TopProcessor;
import com.ajlopez.blockchain.net.http.HttpServer;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    public static void main(String[] args) throws IOException {
        BlockChain blockChain = new BlockChain();
        Block genesis = GenesisGenerator.generateGenesis();
        blockChain.connectBlock(genesis);

        ArgumentsProcessor argsproc = processArguments(args);

        blockChain.onBlock(Start::printBlock);

        String coinbaseText = argsproc.getString("coinbase");

        Address coinbase = coinbaseText.isEmpty() ? Address.ZERO : new Address(HexUtils.hexStringToBytes(coinbaseText));

        NetworkConfiguration networkConfiguration = new NetworkConfiguration(1);
        NodeRunner runner = new NodeRunner(blockChain, argsproc.getBoolean("miner"), argsproc.getInteger("port"), argsproc.getStringList("peers"), coinbase, networkConfiguration);

        runner.start();
        Runtime.getRuntime().addShutdownHook(new Thread(runner::stop));

        TopProcessor topProcessor = new TopProcessor();
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);
        NetworkProcessor networkProcessor = new NetworkProcessor(networkConfiguration);

        topProcessor.registerProcess("eth_blockNumber", blocksProcessor);
        topProcessor.registerProcess("eth_getBlockByNumber", blocksProcessor);
        topProcessor.registerProcess("net_version", networkProcessor);

        HttpServer server = new HttpServer(argsproc.getInteger("rpcport"), topProcessor);

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    public static ArgumentsProcessor processArguments(String[] args) {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 0);
        processor.defineInteger("rp", "rpcport", 4445);
        processor.defineStringList("ps", "peers", "");
        processor.defineBoolean("m", "miner", false);
        processor.defineString("k", "coinbase", "");

        processor.processArguments(args);

        return processor;
    }

    public static void printBlock(Block block) {
        System.out.println(String.format("Connecting block %d %s", block.getNumber(), block.getHash()));
    }
}

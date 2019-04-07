package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
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

        NodeRunner runner = new NodeRunner(blockChain, argsproc.getBoolean("miner"), argsproc.getInteger("port"), argsproc.getStringList("peers"), coinbase, new NetworkConfiguration(1));

        runner.start();
        Runtime.getRuntime().addShutdownHook(new Thread(runner::stop));
    }

    public static ArgumentsProcessor processArguments(String[] args) {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 0);
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

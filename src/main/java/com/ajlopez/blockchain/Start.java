package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.processors.TransactionProcessor;
import com.ajlopez.blockchain.store.MemoryStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    public static void main(String[] args) throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);
        TransactionPool transactionPool = new TransactionPool();
        // TODO processor only uses pool?
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionPool);
        Block genesis = GenesisGenerator.generateGenesis();
        blockChain.connectBlock(genesis);

        ArgumentsProcessor argsproc = processArguments(args);

        blockChain.onBlock(Start::printBlock);

        String coinbaseText = argsproc.getString("coinbase");
        Address coinbase = coinbaseText.isEmpty() ? Address.ZERO : new Address(HexUtils.hexStringToBytes(coinbaseText));
        boolean isMiner = argsproc.getBoolean("miner");
        int port = argsproc.getInteger("port");
        List<String> peers = argsproc.getStringList("peers");

        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)1);
        NodeRunner runner = new NodeRunner(blockChain, isMiner, port, peers, coinbase, networkConfiguration, stores);

        runner.start();
        Runtime.getRuntime().addShutdownHook(new Thread(runner::stop));

        boolean rpc = argsproc.getBoolean("rpc");

        if (rpc) {
            int rpcport = argsproc.getInteger("rpcport");

            RpcRunner rpcrunner = new RpcRunner(rpcport, blockChain, null, transactionPool, transactionProcessor, networkConfiguration);
            Runtime.getRuntime().addShutdownHook(new Thread(rpcrunner::stop));
        }
    }

    public static ArgumentsProcessor processArguments(String[] args) {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 0);

        processor.defineBoolean("r", "rpc", false);
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

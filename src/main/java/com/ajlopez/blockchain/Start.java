package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.peers.TcpPeerServer;
import com.ajlopez.blockchain.processors.NodeProcessor;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    public static void main(String[] args) {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        blockChain.connectBlock(genesis);

        ArgumentsProcessor argsproc = processArguments(args);

        blockChain.onBlock(Start::printBlock);
        NodeProcessor processor = new NodeProcessor(Peer.createRandomPeer(), blockChain);

        processor.startMessagingProcess();

        int port = argsproc.getInteger("port");

        if (port > 0) {
            TcpPeerServer server = new TcpPeerServer(port, processor);
            server.start();
        }

        if (argsproc.getBoolean("miner"))
            processor.startMiningProcess();
    }

    public static ArgumentsProcessor processArguments(String[] args) {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 0);
        processor.defineBoolean("m", "miner", false);

        processor.processArguments(args);

        return processor;
    }

    public static void printBlock(Block block) {
        System.out.println(String.format("Connecting block %d %s", block.getNumber(), block.getHash()));
    }
}

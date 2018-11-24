package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.NodeProcessor;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    public static void main(String[] args) {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        blockChain.connectBlock(genesis);

        blockChain.onBlock(Start::printBlock);
        NodeProcessor processor = new NodeProcessor(Peer.createRandomPeer(), blockChain);

        processor.startMessagingProcess();
        processor.startMiningProcess();
    }

    public static void printBlock(Block block) {
        System.out.println(String.format("Connecting block %d %s", block.getNumber(), block.getHash()));
    }
}

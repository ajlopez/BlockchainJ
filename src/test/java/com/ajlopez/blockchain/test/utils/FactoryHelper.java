package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.*;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.utils.HashUtilsTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 26/01/2018.
 */
public class FactoryHelper {
    public static Transaction createTransaction(int value) {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger bivalue = BigInteger.valueOf(value);
        Random random = new Random();
        int nonce = Math.abs(random.nextInt());

        return new Transaction(sender, receiver, bivalue, nonce);
    }

    public static BlockChain createBlockChainWithGenesis() {
        Block genesis = new Block(0, null, Trie.EMPTY_TRIE_HASH);
        BlockChain blockChain = new BlockChain();
        blockChain.connectBlock(genesis);

        return blockChain;
    }

    public static BlockProcessor createBlockProcessor() {
        return new BlockProcessor(new BlockChain(), new OrphanBlocks());
    }

    public static BlockProcessor createBlockProcessor(BlockChain blockChain) {
        return new BlockProcessor(blockChain, new OrphanBlocks());
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor) {
        return new MessageProcessor(blockProcessor, null, null, null);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, PeerProcessor peerProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, peerProcessor, outputProcessor);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, null, outputProcessor);
    }

    public static MessageProcessor createMessageProcessor(TransactionProcessor transactionProcessor) {
        return new MessageProcessor(null, transactionProcessor, null, null);
    }

    public static MessageProcessor createMessageProcessor(TransactionProcessor transactionProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(null, transactionProcessor, null, outputProcessor);
    }

    public static Peer createRandomPeer() {
        return Peer.createRandomPeer();
    }

    public static NodeProcessor createNodeProcessor() {
        return createNodeProcessor(new BlockChain());
    }

    public static NodeProcessor createNodeProcessor(BlockChain blockChain) {
        return new NodeProcessor(createRandomPeer(), blockChain, new TrieStore(new HashMapStore()));
    }

    public static List<Block> createBlocks(int nblocks) {
        List<Block> blocks = new ArrayList<>();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash());

        blocks.add(block);

        for (int k = 0; k < nblocks; k++) {
            block = new Block(block.getNumber() + 1, block.getHash(), HashUtilsTest.generateRandomHash());
            blocks.add(block);
        }

        return blocks;
    }

    public static Address createRandomAddress() {
        return new Address();
    }
}

package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.*;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.utils.HashUtilsTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 26/01/2018.
 */
public class FactoryHelper {
    private static Random random = new Random();

    public static byte[] createRandomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return bytes;
    }

    public static BlockHash createRandomBlockHash() {
        return new BlockHash(createRandomBytes(BlockHash.HASH_BYTES));
    }

    public static Address createRandomAddress() {
        return new Address(createRandomBytes(Address.ADDRESS_BYTES));
    }

    public static List<Address> createRandomAddresses(int n) {
        List<Address> addresses = new ArrayList<>(n);

        for (int k = 0; k < n; k++)
            addresses.add(createRandomAddress());

        return addresses;
    }

    public static Transaction createTransaction(int value) {
        Address sender = createRandomAddress();
        Address receiver = createRandomAddress();
        BigInteger bivalue = BigInteger.valueOf(value);

        return new Transaction(sender, receiver, bivalue, 0);
    }

    public static void extendBlockChainWithBlocks(BlockChain blockChain, int nblocks) {
        Block block = blockChain.getBestBlock();
        Address coinbase = FactoryHelper.createRandomAddress();

        for (int k = 0; k < nblocks; k++) {
            Block newBlock = new Block(block.getNumber() + 1, block.getHash(), block.getStateRootHash(), System.currentTimeMillis() / 1000, coinbase);
            blockChain.connectBlock(newBlock);
            block = newBlock;
        }
    }

    public static BlockChain createBlockChainWithGenesis() {
        return createBlockChainWithGenesis(GenesisGenerator.generateGenesis());
    }

    public static BlockChain createBlockChainWithGenesis(AccountStore accountStore) {
        return createBlockChainWithGenesis(GenesisGenerator.generateGenesis(accountStore));
    }

    public static BlockChain createBlockChainWithGenesis(Block genesis) {
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
        return new NodeProcessor(null, createRandomPeer(), blockChain, new TrieStore(new HashMapStore()), createRandomAddress());
    }

    public static List<Block> createBlocks(int nblocks) {
        Address coinbase = createRandomAddress();
        List<Block> blocks = new ArrayList<>();

        Block block = new Block(0, null, HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);

        blocks.add(block);

        for (int k = 0; k < nblocks; k++) {
            block = new Block(block.getNumber() + 1, block.getHash(), HashUtilsTest.generateRandomHash(), System.currentTimeMillis() / 1000, coinbase);
            blocks.add(block);
        }

        return blocks;
    }
}

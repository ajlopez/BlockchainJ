package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.BlockConsumer;
import com.ajlopez.blockchain.test.BlocksConsumer;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 17/12/2017.
 */
public class BlockProcessorTest {
    @Test
    public void noBestBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBestBlockInformation());
    }

    @Test
    public void noBlockByHash() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBlockByHash(FactoryHelper.createRandomBlockHash()));
    }

    @Test
    public void noBlockByNumber() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBlockByNumber(1));
    }

    @Test
    public void notChainedBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isChainedBlock(FactoryHelper.createRandomBlockHash()));
    }

    @Test
    public void notOrphanBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isOrphanBlock(FactoryHelper.createRandomBlockHash()));
    }

    @Test
    public void unknownBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isKnownBlock(FactoryHelper.createRandomBlockHash()));
    }

    @Test
    public void addFirstBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> processedBlocks = processor.processBlock(block);

        Assert.assertNotNull(processedBlocks);
        Assert.assertFalse(processedBlocks.isEmpty());
        Assert.assertEquals(1, processedBlocks.size());
        Assert.assertEquals(block, processedBlocks.get(0));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertEquals(block.getHash(), processor.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), processor.getBlockByNumber(block.getNumber()).getHash());
    }

    @Test
    public void rejectBlockByInvalidTransactionRoot() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block block0 = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);
        Block block = new Block(block0.getHeader(), null, transactions);

        List<Block> processedBlocks = processor.processBlock(block);

        Assert.assertNotNull(processedBlocks);
        Assert.assertTrue(processedBlocks.isEmpty());

        Assert.assertNull(processor.getBestBlockInformation());
    }

    @Test
    public void addFirstBlockCheckingTransactionPool() throws IOException {
        BlockChain blockChain = new BlockChain(new MemoryStores());
        TransactionPool transactionPool = new TransactionPool();
        BlockProcessor processor = FactoryHelper.createBlockProcessor(blockChain, transactionPool);
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> processedBlocks = processor.processBlock(block);

        Assert.assertNotNull(processedBlocks);
        Assert.assertFalse(processedBlocks.isEmpty());
        Assert.assertEquals(1, processedBlocks.size());
        Assert.assertEquals(block, processedBlocks.get(0));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertEquals(block.getHash(), processor.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), processor.getBlockByNumber(block.getNumber()).getHash());

        Assert.assertTrue(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void addFirstBlockAndEmitNewBestBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        BlockConsumer consumer = new BlockConsumer();

        processor.onNewBlock(consumer);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> processedBlocks = processor.processBlock(block);

        Assert.assertNotNull(processedBlocks);
        Assert.assertFalse(processedBlocks.isEmpty());
        Assert.assertEquals(1, processedBlocks.size());
        Assert.assertEquals(block, processedBlocks.get(0));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertEquals(block.getHash(), processor.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), processor.getBlockByNumber(block.getNumber()).getHash());

        Assert.assertNotNull(consumer.getBlock());
        Assert.assertEquals(block.getHash(), consumer.getBlock().getHash());
    }

    @Test
    public void addFirstBlockAndEmitNewBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        BlockConsumer consumer = new BlockConsumer();

        processor.onNewBlock(consumer);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> processedBlocks = processor.processBlock(block);

        Assert.assertNotNull(processedBlocks);
        Assert.assertFalse(processedBlocks.isEmpty());
        Assert.assertEquals(1, processedBlocks.size());
        Assert.assertEquals(block, processedBlocks.get(0));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertEquals(block.getHash(), processor.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), processor.getBlockByNumber(block.getNumber()).getHash());

        Assert.assertNotNull(consumer.getBlock());
        Assert.assertEquals(block.getHash(), consumer.getBlock().getHash());
    }

    @Test
    public void addOrphanBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> connectedBlocks = processor.processBlock(block);

        Assert.assertNotNull(connectedBlocks);
        Assert.assertTrue(connectedBlocks.isEmpty());

        Assert.assertNull(processor.getBestBlockInformation());
    }

    @Test
    public void addManyBlocks() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = FactoryHelper.createBlockChain(stores, 2, 4);

        Assert.assertEquals(2, blockChain.getBestBlockInformation().getBlockNumber());

        BlockProcessor processor = new BlockProcessor(new BlockChain(new MemoryStores()), new OrphanBlocks(), FactoryHelper.createBlockValidator(new AccountStoreProvider(stores.getAccountTrieStore())), new TransactionPool());

        for (int k = 0; k <= 2; k++)
            processor.processBlock(blockChain.getBlockByNumber(k));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(2, processor.getBestBlockInformation().getBlockNumber());
        Assert.assertEquals(Difficulty.THREE, processor.getBestBlockInformation().getTotalDifficulty());
    }

    @Test
    public void addManyBlocksRemovingThemFromTransactionPool() throws IOException {
        Stores stores = new MemoryStores();
        TransactionPool transactionPool = new TransactionPool();
        BlockChain blockChain = FactoryHelper.createBlockChain(stores,2, 4);

        Assert.assertEquals(2, blockChain.getBestBlockInformation().getBlockNumber());

        BlockProcessor processor = new BlockProcessor(new BlockChain(new MemoryStores()), new OrphanBlocks(), FactoryHelper.createBlockValidator(new AccountStoreProvider(stores.getAccountTrieStore())), transactionPool);

        for (int k = 0; k <= 2; k++) {
            Block block = blockChain.getBlockByNumber(k);

            for (Transaction transaction : block.getTransactions())
                transactionPool.addTransaction(transaction);
        }

        for (int k = 0; k <= 2; k++)
            processor.processBlock(blockChain.getBlockByNumber(k));

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(2, processor.getBestBlockInformation().getBlockNumber());
        Assert.assertEquals(Difficulty.THREE, processor.getBestBlockInformation().getTotalDifficulty());

        Assert.assertTrue(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void getUnknownAncestorHash() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        List<Block> connectedBlocks = processor.processBlock(block);

        Assert.assertNotNull(connectedBlocks);
        Assert.assertTrue(connectedBlocks.isEmpty());

        Assert.assertNull(processor.getBestBlockInformation());

        BlockHash hash = processor.getUnknownAncestorHash(block.getHash());

        Assert.assertNotNull(hash);
        Assert.assertEquals(block.getParentHash(), hash);
    }

    @Test
    public void getNotOrphanUnknownAncestorHash() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, FactoryHelper.createRandomBlockHash(), null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        BlockHash hash = processor.getUnknownAncestorHash(block.getHash());

        Assert.assertNotNull(hash);
        Assert.assertEquals(block.getHash(), hash);
    }

    @Test
    public void getNullUnknownAncestorHash() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        BlockHash hash = processor.getUnknownAncestorHash(null);

        Assert.assertNull(hash);
    }

    @Test
    public void addOrphanBlockAndNoEmitNewBestBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        BlockConsumer consumer = new BlockConsumer();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        processor.onNewBlock(consumer);
        processor.processBlock(block);

        Assert.assertNull(processor.getBestBlockInformation());
        Assert.assertNull(consumer.getBlock());
    }

    @Test
    public void addOrphanBlockAndNoEmitNewBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        BlockConsumer consumer = new BlockConsumer();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        processor.onNewBlock(consumer);
        processor.processBlock(block);

        Assert.assertNull(processor.getBestBlockInformation());
        Assert.assertNull(consumer.getBlock());
    }

    @Test
    public void switchToABetterForkUsingOrphan() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0, 0, null, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);
        Block block2 = new Block(2, block1.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);
        Block block3 = new Block(3, block2.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        processor.processBlock(genesis);
        processor.processBlock(block1);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertNotNull(processor.getBestBlockInformation().getBlockHash());
        Assert.assertEquals(block1.getHash(), processor.getBestBlockInformation().getBlockHash());

        processor.processBlock(block3);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block1.getNumber(), processor.getBestBlockInformation().getBlockNumber());
        Assert.assertEquals(block1.getHash(), processor.getBestBlockInformation().getBlockHash());

        BlockConsumer consumer = new BlockConsumer();
        BlocksConsumer consumer2 = new BlocksConsumer();

        processor.onNewBlock(consumer);
        processor.onNewBlock(consumer2);

        processor.processBlock(block2);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block3.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertNotNull(consumer.getBlock());
        Assert.assertEquals(block3.getHash(), consumer.getBlock().getHash());

        Assert.assertNotNull(consumer2.getBlocks());
        Assert.assertFalse(consumer2.getBlocks().isEmpty());
        Assert.assertEquals(2, consumer2.getBlocks().size());
        Assert.assertEquals(block2.getHash(), consumer2.getBlocks().get(0).getHash());
        Assert.assertEquals(block3.getHash(), consumer2.getBlocks().get(1).getHash());

        Assert.assertEquals(genesis.getHash(), processor.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(block1.getHash(), processor.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block2.getHash(), processor.getBlockByHash(block2.getHash()).getHash());
        Assert.assertEquals(block3.getHash(), processor.getBlockByHash(block3.getHash()).getHash());
    }

    @Test
    public void switchToABetterForkUsingOrphanAndEmitNewBestBlock() throws IOException {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0, 0, null, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);
        Block block2 = new Block(2, block1.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);
        Block block3 = new Block(3, block2.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0, null, 0);

        processor.processBlock(genesis);
        processor.processBlock(block1);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertNotNull(processor.getBestBlockInformation().getBlockHash());
        Assert.assertEquals(block1.getHash(), processor.getBestBlockInformation().getBlockHash());

        processor.processBlock(block3);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block1.getNumber(), processor.getBestBlockInformation().getBlockNumber());
        Assert.assertEquals(block1.getHash(), processor.getBestBlockInformation().getBlockHash());

        BlockConsumer consumer = new BlockConsumer();

        processor.onNewBlock(consumer);

        processor.processBlock(block2);

        Assert.assertNotNull(processor.getBestBlockInformation());
        Assert.assertEquals(block3.getHash(), processor.getBestBlockInformation().getBlockHash());

        Assert.assertEquals(genesis.getHash(), processor.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(block1.getHash(), processor.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block2.getHash(), processor.getBlockByHash(block2.getHash()).getHash());
        Assert.assertEquals(block3.getHash(), processor.getBlockByHash(block3.getHash()).getHash());

        Assert.assertNotNull(consumer.getBlock());
        Assert.assertEquals(block3.getHash(), consumer.getBlock().getHash());
    }
}

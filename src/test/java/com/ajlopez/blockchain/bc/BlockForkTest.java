package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 14/10/2019.
 */
public class BlockForkTest {
    @Test
    public void processTwoBestBlocks() throws IOException {
        Address senderAddress = FactoryHelper.createRandomAddress();
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        BlockChain blockChain = FactoryHelper.createBlockChainWithAccount(senderAddress, 1000000, trieStore, 0, 0);

        FactoryHelper.extendBlockChainWithBlocks(accountStoreProvider, blockChain, 3, 0, null, 0);

        Block firstBestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(firstBestBlock);
        Assert.assertEquals(3, firstBestBlock.getNumber());

        List<Block> toBeRemoved = new ArrayList<>();

        for (int k = 1; k < 3; k++)
            toBeRemoved.add(blockChain.getBlockByNumber(k));

        FactoryHelper.extendBlockChainWithBlocksFromBlock(accountStoreProvider, blockChain, blockChain.getBlockByNumber(0), 5, 0, null, 0);

        Block secondBestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(secondBestBlock);
        Assert.assertEquals(5, secondBestBlock.getNumber());

        List<Block> toBeAdded = new ArrayList<>();

        for (int k = 1; k < 5; k++)
            toBeAdded.add(blockChain.getBlockByNumber(k));

        BlockFork blockFork = BlockFork.fromBlocks(blockChain, firstBestBlock, secondBestBlock);

        Assert.assertNotNull(blockFork);
        Assert.assertNotNull(blockFork.getOldBlocks());
        Assert.assertEquals(3, blockFork.getOldBlocks().size());
        Assert.assertNotNull(blockFork.getNewBlocks());
        Assert.assertEquals(5, blockFork.getNewBlocks().size());

        for (Block b : toBeRemoved)
            Assert.assertTrue(blockFork.getOldBlocks().contains(b));

        for (Block b : toBeAdded)
            Assert.assertTrue(blockFork.getNewBlocks().contains(b));
    }

    @Test
    public void getTransactions() throws IOException {
        Address senderAddress = FactoryHelper.createRandomAddress();
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        BlockChain blockChain = FactoryHelper.createBlockChainWithAccount(senderAddress, 1000000, trieStore, 10, 10);

        Block firstBestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(firstBestBlock);
        Assert.assertEquals(10, firstBestBlock.getNumber());

        List<Transaction> toBeRemoved = new ArrayList<>();

        for (int k = 1; k < 10; k++) {
            Block block = blockChain.getBlockByNumber(k);

            for (Transaction transaction : block.getTransactions())
                toBeRemoved.add(transaction);
        }

        FactoryHelper.extendBlockChainWithBlocksFromBlock(accountStoreProvider, blockChain, blockChain.getBlockByNumber(0), 15, 10, senderAddress, 0);

        Block secondBestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(secondBestBlock);
        Assert.assertEquals(15, secondBestBlock.getNumber());

        List<Transaction> toBeAdded = new ArrayList<>();

        for (int k = 1; k < 15; k++) {
            Block block = blockChain.getBlockByNumber(k);

            for (Transaction transaction : block.getTransactions())
                toBeAdded.add(transaction);
        }

        BlockFork blockFork = BlockFork.fromBlocks(blockChain, firstBestBlock, secondBestBlock);

        Assert.assertNotNull(blockFork);
        Assert.assertNotNull(blockFork.getOldTransactions());
        Assert.assertEquals(10 * 10, blockFork.getOldTransactions().size());
        Assert.assertNotNull(blockFork.getNewTransactions());
        Assert.assertEquals(15 * 10, blockFork.getNewTransactions().size());

        for (Transaction transaction : toBeRemoved)
            Assert.assertTrue(blockFork.getOldTransactions().contains(transaction));

        for (Transaction transaction : toBeAdded)
            Assert.assertTrue(blockFork.getNewTransactions().contains(transaction));
    }

    @Test
    public void processOneBestBlocks() throws IOException {
        Address senderAddress = FactoryHelper.createRandomAddress();
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        BlockChain blockChain = FactoryHelper.createBlockChainWithAccount(senderAddress, 1000000, trieStore, 0, 0);

        FactoryHelper.extendBlockChainWithBlocks(accountStoreProvider, blockChain, 3, 0, null, 0);

        Block bestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertEquals(3, bestBlock.getNumber());

        List<Block> toBeAdded = new ArrayList<>();

        for (int k = 0; k <= 3; k++)
            toBeAdded.add(blockChain.getBlockByNumber(k));

        BlockFork blockFork = BlockFork.fromBlocks(blockChain, null, bestBlock);

        Assert.assertNotNull(blockFork);
        Assert.assertNotNull(blockFork.getOldBlocks());
        Assert.assertEquals(0, blockFork.getOldBlocks().size());
        Assert.assertNotNull(blockFork.getNewBlocks());
        Assert.assertEquals(4, blockFork.getNewBlocks().size());

        for (Block b : toBeAdded)
            Assert.assertTrue(blockFork.getNewBlocks().contains(b));
    }
}

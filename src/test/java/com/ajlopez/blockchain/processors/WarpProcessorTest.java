package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by ajlopez on 24/06/2019.
 */
public class WarpProcessorTest {
    @Test
    public void processEmptyBlock() throws IOException {
        Block block = GenesisGenerator.generateGenesis();
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Hash> hashes = processor.processBlock(block);

        Assert.assertNotNull(hashes);
        Assert.assertTrue(hashes.isEmpty());
    }

    @Test
    public void noPendingHashesForRandomTopHash() {
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Hash> hashes = processor.getPendingAccountHashes(FactoryHelper.createRandomHash());

        Assert.assertNotNull(hashes);
        Assert.assertTrue(hashes.isEmpty());
    }

    @Test
    public void processBlockWithTransactions() throws IOException {
        Block block = FactoryHelper.createBlockChain(1, 10).getBlockByNumber(1);
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Hash> hashes = processor.processBlock(block);

        Assert.assertNotNull(hashes);
        Assert.assertFalse(hashes.isEmpty());
        Assert.assertTrue(hashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, hashes.size());

        Set<Hash> pendingHashes = processor.getPendingAccountHashes(block.getStateRootHash());

        Assert.assertNotNull(pendingHashes);
        Assert.assertFalse(pendingHashes.isEmpty());
        Assert.assertTrue(pendingHashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, pendingHashes.size());
    }

    @Test
    public void processBlockWithTransactionsTwice() throws IOException {
        Block block = FactoryHelper.createBlockChain(1, 10).getBlockByNumber(1);
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        processor.processBlock(block);
        Set<Hash> hashes = processor.processBlock(block);

        Assert.assertNotNull(hashes);
        Assert.assertFalse(hashes.isEmpty());
        Assert.assertTrue(hashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, hashes.size());

        Set<Hash> pendingHashes = processor.getPendingAccountHashes(block.getStateRootHash());

        Assert.assertNotNull(pendingHashes);
        Assert.assertFalse(pendingHashes.isEmpty());
        Assert.assertTrue(pendingHashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, pendingHashes.size());
    }

    @Test
    public void processBlockWithTransactionsAndTopNode() throws IOException {
        Stores stores = new MemoryStores();

        Block block = FactoryHelper.createBlockChain(stores, 1, 10).getBlockByNumber(1);

        Assert.assertNotNull(stores.getAccountStoreProvider().retrieve(block.getStateRootHash()));

        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Hash> hashes = processor.processBlock(block);

        Assert.assertNotNull(hashes);
        Assert.assertFalse(hashes.isEmpty());
        Assert.assertTrue(hashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, hashes.size());

        Set<Hash> result = processor.processAccountNode(block.getStateRootHash(), stores.getAccountTrieStore().retrieve(block.getStateRootHash()).getEncoded());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(accountStore.exists(block.getStateRootHash()));
    }


    @Test
    public void processAccountNodeWithRandomTopHash() throws IOException {
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Hash> result = processor.processAccountNode(FactoryHelper.createRandomHash(), FactoryHelper.createRandomBytes(42));

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void copyCompleteStateOfBlockWithTransactions() throws IOException {
        Stores stores = new MemoryStores();

        BlockChain blockChain = FactoryHelper.createBlockChain(stores, 1, 10);
        Block genesis = blockChain.getBlockByNumber(0);
        Block block = blockChain.getBlockByNumber(1);

        Assert.assertNotEquals(genesis.getStateRootHash(), block.getStateRootHash());
        Assert.assertFalse(block.getTransactions().isEmpty());
        Assert.assertEquals(10, block.getTransactions().size());

        Assert.assertTrue(stores.getAccountTrieStore().exists(block.getStateRootHash()));

        TrieStore accountTrieStore = new TrieStore(new HashMapStore());
        AccountStore accountStore0 = new AccountStore(stores.getAccountTrieStore().retrieve(block.getStateRootHash()));

        for (Transaction transaction : block.getTransactions()) {
            Account account0 = accountStore0.getAccount(transaction.getReceiver());
            Assert.assertEquals(transaction.getValue(), account0.getBalance());
            Assert.assertEquals(0, account0.getNonce());
        }

        WarpProcessor processor = new WarpProcessor(accountTrieStore);

        processor.processBlock(block);

        Hash topHash = block.getStateRootHash();

        for (Set<Hash> pendingHashes = processor.getPendingAccountHashes(topHash); !pendingHashes.isEmpty(); pendingHashes = processor.getPendingAccountHashes(topHash))
            for (Hash pendingHash : pendingHashes)
                processor.processAccountNode(topHash, stores.getAccountTrieStore().retrieve(pendingHash).getEncoded());

        Assert.assertTrue(accountTrieStore.exists(block.getStateRootHash()));

        AccountStore accountStore = new AccountStore(accountTrieStore.retrieve(topHash));

        for (Transaction transaction : block.getTransactions()) {
            Account account = accountStore.getAccount(transaction.getReceiver());
            Assert.assertEquals(transaction.getValue(), account.getBalance());
            Assert.assertEquals(0, account.getNonce());
        }
    }

    @Test
    public void expectedBlocks() {
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        Set<Long> result1 = processor.getExpectedBlocks();

        Assert.assertNotNull(result1);
        Assert.assertTrue(result1.isEmpty());

        processor.expectBlock(42);

        Set<Long> result2 = processor.getExpectedBlocks();

        Assert.assertNotNull(result2);
        Assert.assertFalse(result2.isEmpty());
        Assert.assertTrue(result2.contains(Long.valueOf(42)));
        Assert.assertEquals(1, result2.size());

        processor.expectBlock(3);

        Set<Long> result3 = processor.getExpectedBlocks();

        Assert.assertNotNull(result3);
        Assert.assertFalse(result3.isEmpty());
        Assert.assertTrue(result3.contains(Long.valueOf(42)));
        Assert.assertTrue(result3.contains(Long.valueOf(3)));
        Assert.assertEquals(2, result3.size());
    }
}

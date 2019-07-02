package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by ajlopez on 24/06/2019.
 */
public class WarpProcessorTest {
    @Test
    public void processEmptyBlock() {
        Block block = GenesisGenerator.generateGenesis();
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        List<Hash> hashes = processor.processBlock(block);

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
    public void processBlockWithTransactions() {
        Block block = FactoryHelper.createBlockChain(1, 10).getBlockByNumber(1);
        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        List<Hash> hashes = processor.processBlock(block);

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
    public void processBlockWithTransactionsAndTopNode() {
        KeyValueStore keyValueStore0 = new HashMapStore();
        TrieStore trieStore0 = new TrieStore(keyValueStore0);

        Block block = FactoryHelper.createBlockChain(trieStore0, 1, 10).getBlockByNumber(1);

        Assert.assertTrue(trieStore0.exists(block.getStateRootHash()));

        TrieStore accountStore = new TrieStore(new HashMapStore());

        WarpProcessor processor = new WarpProcessor(accountStore);

        List<Hash> hashes = processor.processBlock(block);

        Assert.assertNotNull(hashes);
        Assert.assertFalse(hashes.isEmpty());
        Assert.assertTrue(hashes.contains(block.getStateRootHash()));
        Assert.assertEquals(1, hashes.size());

        Set<Hash> result = processor.processAccountNode(block.getStateRootHash(), trieStore0.retrieve(block.getStateRootHash()).getEncoded());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(accountStore.exists(block.getStateRootHash()));
    }

    @Test
    public void copyCompleteStateOfBlockWithTransactions() {
        KeyValueStore keyValueStore0 = new HashMapStore();
        TrieStore trieStore0 = new TrieStore(keyValueStore0);

        BlockChain blockChain = FactoryHelper.createBlockChain(trieStore0, 1, 10);
        Block genesis = blockChain.getBlockByNumber(0);
        Block block = blockChain.getBlockByNumber(1);

        Assert.assertNotEquals(genesis.getStateRootHash(), block.getStateRootHash());
        Assert.assertFalse(block.getTransactions().isEmpty());
        Assert.assertEquals(10, block.getTransactions().size());

        Assert.assertTrue(trieStore0.exists(block.getStateRootHash()));

        TrieStore accountTrieStore = new TrieStore(new HashMapStore());
        AccountStore accountStore0 = new AccountStore(trieStore0.retrieve(block.getStateRootHash()));

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
                processor.processAccountNode(topHash, trieStore0.retrieve(pendingHash).getEncoded());

        Assert.assertTrue(accountTrieStore.exists(block.getStateRootHash()));

        AccountStore accountStore = new AccountStore(accountTrieStore.retrieve(topHash));

        for (Transaction transaction : block.getTransactions()) {
            Account account = accountStore.getAccount(transaction.getReceiver());
            Assert.assertEquals(transaction.getValue(), account.getBalance());
            Assert.assertEquals(0, account.getNonce());
        }
    }
}

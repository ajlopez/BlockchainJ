package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
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

        List<Hash> result = processor.processAccountNode(block.getStateRootHash(), trieStore0.retrieve(block.getStateRootHash()).getEncoded());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(accountStore.exists(block.getStateRootHash()));
    }
}

package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
}

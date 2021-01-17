package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BlockStoreTest {
    @Test
    public void retrieveUnknowBlockAsNull() throws IOException {
        BlockStore blockStore = new BlockStore(new HashMapStore());
        BlockHash hash = FactoryHelper.createRandomBlockHash();

        Assert.assertNull(blockStore.getBlock(hash));
    }
}

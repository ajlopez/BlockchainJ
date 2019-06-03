package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 03/06/2019.
 */
public class BlockValidatorTest {
    @Test
    public void validEmptyBlock() {
        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(block, genesis.getStateRootHash()));
    }
}

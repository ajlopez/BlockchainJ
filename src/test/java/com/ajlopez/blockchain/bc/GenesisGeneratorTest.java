package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 29/11/2018.
 */
public class GenesisGeneratorTest {
    @Test
    public void generateGenesisWithNoInitialAccounts() {
        Block genesis = GenesisGenerator.generateGenesis();

        Assert.assertNotNull(genesis);
        Assert.assertEquals(0, genesis.getNumber());
        Assert.assertNotNull(genesis.getParentHash());
        Assert.assertEquals(BlockHash.EMPTY_BLOCK_HASH, genesis.getParentHash());
        Assert.assertNotNull(genesis.getStateRootHash());
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, genesis.getStateRootHash());
        Assert.assertNotNull(genesis.getTransactions());
        Assert.assertTrue(genesis.getTransactions().isEmpty());
    }

    @Test
    public void generateGenesisWithInitialAccounts() {
        AccountStore accountStore = new AccountStore(new Trie());

        accountStore.putAccount(FactoryHelper.createRandomAddress(), new Account(BigInteger.TEN, 42));

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Assert.assertNotNull(genesis);
        Assert.assertEquals(0, genesis.getNumber());
        Assert.assertNotNull(genesis.getParentHash());
        Assert.assertEquals(BlockHash.EMPTY_BLOCK_HASH, genesis.getParentHash());
        Assert.assertNotNull(genesis.getStateRootHash());
        Assert.assertEquals(accountStore.getRootHash(), genesis.getStateRootHash());
        Assert.assertNotNull(genesis.getTransactions());
        Assert.assertTrue(genesis.getTransactions().isEmpty());
    }
}

package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class WorldTest {
    @Test
    public void getUnknownAccount() {
        World world = new World();

        Assert.assertNull(world.getAccount("foo"));
    }

    @Test
    public void setAndGetAccount() {
        World world = new World();
        Account account = new Account();

        world.setAccount("acc1", account);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }

    @Test
    public void getAccountAddress() {
        World world = new World();
        Account account = new Account();

        world.setAccount("acc1", account);

        Address result = world.getAccountAddress("acc1");

        Assert.assertNotNull(result);
    }

    @Test
    public void getUnknownAccountAddress() {
        World world = new World();
        Account account = new Account();

        Address result = world.getAccountAddress("acc1");

        Assert.assertNull(result);
    }

    @Test
    public void getUnknownBlock() {
        World world = new World();

        Assert.assertNull(world.getBlock("blk1"));
    }

    @Test
    public void setAndGetBlock() {
        World world = new World();
        Block block = FactoryHelper.createBlocks(1).get(0);

        world.setBlock("blk1", block);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(BlockEncoder.encode(block), BlockEncoder.encode(result));
    }

    @Test
    public void getGenesisBlock() {
        World world = new World();

        Block result = world.getBlock("genesis");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getNumber());

        BlockChain blockChain = world.getBlockChain();

        Assert.assertNotNull(blockChain);
        Assert.assertArrayEquals(BlockEncoder.encode(blockChain.getBestBlock()), BlockEncoder.encode(result));
    }

    @Test
    public void getBlockChain() {
        World world = new World();
        BlockChain blockChain = world.getBlockChain();

        Assert.assertNotNull(blockChain);
        Assert.assertEquals(0, blockChain.getBestBlockNumber());

        Block block = blockChain.getBestBlock();

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getNumber());
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, block.getStateRootHash());
    }

    @Test
    public void getBlockChainWithInitialAccounts() {
        World world = new World();
        Account account1 = new Account(BigInteger.TEN, 10, null, null);
        Account account2 = new Account(BigInteger.ONE, 20, null, null);

        world.setAccount("acc1", account1);
        world.setAccount("acc2", account2);

        BlockChain blockChain = world.getBlockChain();

        Assert.assertNotNull(blockChain);
        Assert.assertEquals(0, blockChain.getBestBlockNumber());

        Block block = blockChain.getBestBlock();

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getNumber());
        Assert.assertNotEquals(Trie.EMPTY_TRIE_HASH, block.getStateRootHash());

        AccountStore accountStore = new AccountStore(new Trie());
        accountStore.putAccount(world.getAccountAddress("acc1"), account1);
        accountStore.putAccount(world.getAccountAddress("acc2"), account2);

        Assert.assertEquals(accountStore.getRootHash(), block.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), world.getBlock("genesis").getStateRootHash());
    }
}

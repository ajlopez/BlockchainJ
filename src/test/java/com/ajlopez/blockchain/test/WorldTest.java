package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class WorldTest {
    @Test
    public void getUnknownAccount() throws IOException {
        World world = new World();

        Assert.assertNull(world.getAccount("foo"));
    }

    @Test
    public void setAndGetAccount() throws IOException {
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
    public void getUnknownTransaction() {
        World world = new World();

        Assert.assertNull(world.getTransaction("foo"));
    }

    @Test
    public void getUnknownTransactions() {
        World world = new World();
        List<String> names = new ArrayList<>();
        names.add("tx1");
        names.add("tx2");

        List<Transaction> result = world.getTransactions(names);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void setAndGetTransaction() {
        Transaction transaction = FactoryHelper.createTransaction(1000);

        World world = new World();

        world.setTransaction("tx1", transaction);

        Transaction result = world.getTransaction("tx1");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(TransactionEncoder.encode(transaction), TransactionEncoder.encode(result));
    }

    @Test
    public void setAndGetTransactions() {
        Transaction transaction1 = FactoryHelper.createTransaction(1000);
        Transaction transaction2 = FactoryHelper.createTransaction(2000);

        World world = new World();
        List<String> names = new ArrayList<>();
        names.add("tx1");
        names.add("tx2");

        world.setTransaction("tx1", transaction1);
        world.setTransaction("tx2", transaction2);

        List<Transaction> result = world.getTransactions(names);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction1, result.get(0));
        Assert.assertEquals(transaction2, result.get(1));
    }

    @Test
    public void getUnknownBlock() {
        World world = new World();

        Assert.assertNull(world.getBlock("blk1"));
    }


    @Test
    public void getUnknownBlocks() {
        World world = new World();
        List<String> names = new ArrayList<>();
        names.add("blk1");
        names.add("blk2");

        List<Block> result = world.getBlocks(names);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
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
    public void setAndGetBlocks() {
        World world = new World();
        Block genesis = world.getBlock("genesis");

        Block block1 = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);
        Block block2 = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        List<String> names = new ArrayList<>();
        names.add("blk1");
        names.add("blk2");

        world.setBlock("blk1", block1);
        world.setBlock("blk2", block2);

        List<Block> result = world.getBlocks(names);

        Assert.assertNotNull(result);
        Assert.assertEquals(block1, result.get(0));
        Assert.assertEquals(block2, result.get(1));
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
        Account account1 = new Account(Coin.TEN, 10, null, null);
        Account account2 = new Account(Coin.ONE, 20, null, null);

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

    @Test
    public void getUnknownCode() throws IOException {
        Hash codeHash = FactoryHelper.createRandomHash();
        World world = new World();

        Assert.assertNull(world.getCode(codeHash));
    }

    @Test
    public void setAndGetCode() throws IOException {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash hash = HashUtils.calculateHash(code);

        World world = new World();

        world.setCode(hash, code);

        byte[] result = world.getCode(hash);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(code, result);
    }
}

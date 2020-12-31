package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 11/12/2020.
 */
public class DslDotExpressionTest {
    @Test
    public void evaluateAccountBalance() throws IOException {
        World world = new World();
        Account account = new Account(Coin.TEN, 0, 0, null, null);
        world.setAccount("acc1", account);

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("acc1"), "balance");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Coin);
        Assert.assertEquals(Coin.TEN, result);
    }

    @Test
    public void evaluateAccountNonce() throws IOException {
        World world = new World();
        Account account = new Account(Coin.TEN, 42, 0, null, null);
        world.setAccount("acc1", account);

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("acc1"), "nonce");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Long);
        Assert.assertEquals(42L, result);
    }

    @Test
    public void evaluateTransactionValue() throws IOException {
        World world = new World();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        world.setTransaction("tx1", transaction);

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("tx1"), "value");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Coin);
        Assert.assertEquals(Coin.fromUnsignedLong(1000), result);
    }

    @Test
    public void evaluateBlockNumber() throws IOException {
        World world = new World();
        Block block = FactoryHelper.createBlock(world.getBlock("genesis"), FactoryHelper.createRandomAddress(), 0);
        world.setBlock("b1", block);

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("b1"), "number");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Long);
        Assert.assertEquals(1L, result);
    }

    @Test
    public void evaluateBlockHash() throws IOException {
        World world = new World();
        Block block = FactoryHelper.createBlock(world.getBlock("genesis"), FactoryHelper.createRandomAddress(), 0);
        world.setBlock("b1", block);

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("b1"), "hash");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof BlockHash);
        Assert.assertEquals(block.getHash(), result);
    }

    @Test
    public void evaluateBestBlock() throws IOException {
        World world = new World();
        Block genesis = world.getBlock("genesis");

        DslDotExpression dslDotExpression = new DslDotExpression(new DslTerm("blockchain"), "bestBlock");

        Object result = dslDotExpression.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Block);
        Assert.assertEquals(genesis.getHash(), ((Block) result).getHash());
    }
}

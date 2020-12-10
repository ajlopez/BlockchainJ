package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 08/12/2020.
 */
public class DslTermTest {
    @Test
    public void evaluateAccount() throws IOException {
        World world = new World();
        Account account = new Account(Coin.TEN, 0, 0, null, null);
        world.setAccount("acc1", account);

        DslTerm dslTerm = new DslTerm("acc1");

        Object result = dslTerm.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Account);
        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode((Account)result));
    }

    @Test
    public void evaluateBlock() throws IOException {
        World world = new World();
        Block block = FactoryHelper.createBlock(world.getBlock("genesis"), FactoryHelper.createRandomAddress(), 0);
        world.setBlock("b1", block);

        DslTerm dslTerm = new DslTerm("b1");

        Object result = dslTerm.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Block);
        Assert.assertArrayEquals(BlockEncoder.encode(block), BlockEncoder.encode((Block)result));
    }

    @Test
    public void evaluateBlockchain() throws IOException {
        World world = new World();

        DslTerm dslTerm = new DslTerm("blockchain");

        Object result = dslTerm.evaluate(world);

        Assert.assertNotNull(result);
        Assert.assertSame(world.getBlockChain(), result);
    }
}

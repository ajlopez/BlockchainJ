package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by ajlopez on 19/12/2020.
 */
public class WorldDslProcessorTest {
    @Test
    public void createProcessorWithWorld() throws IOException {
        World world = new World();

        WorldDslProcessor processor = new WorldDslProcessor(world);

        Assert.assertSame(world, processor.getWorld());
    }

    @Test
    public void processAccountNewCommand() throws IOException {
        World world = new World();

        WorldDslProcessor processor = new WorldDslProcessor(world);

        DslParser parser = new DslParser("account acc1");

        processor.processCommands(parser);

        Account account = world.getAccount("acc1");

        Assert.assertNotNull(account);
        Assert.assertEquals(Coin.ZERO, account.getBalance());
    }
}
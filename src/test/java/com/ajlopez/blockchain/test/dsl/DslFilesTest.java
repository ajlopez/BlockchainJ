package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 19/12/2020.
 */
public class DslFilesTest {
    @Test
    public void runAccounts01Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/accounts01.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getAccount("acc1"));
    }

    @Test
    public void runTransactions01Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/transactions01.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getTransaction("tx1"));
    }

    @Test
    public void runTransactions02Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/transactions02.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getTransaction("tx1"));
    }

    @Test
    public void runBlocks01Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blocks01.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
    }

    @Test
    public void runBlockchain01Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain01.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
        Assert.assertEquals(1, world.getBlockChain().getBestBlockInformation().getBlockNumber());
    }

    @Test
    public void runBlockchain02Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain02.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
        Assert.assertNotNull(world.getBlock("b2"));
        Assert.assertEquals(2, world.getBlockChain().getBestBlockInformation().getBlockNumber());
    }

    @Test
    public void runBlockchain03Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain03.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
        Assert.assertNotNull(world.getBlock("b2"));
        Assert.assertEquals(2, world.getBlockChain().getBestBlockInformation().getBlockNumber());
    }

    @Test
    public void runBlockchain04Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain04.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
        Assert.assertNotNull(world.getBlock("b1b"));
        Assert.assertNotNull(world.getBlock("b1c"));
        Assert.assertNotNull(world.getBlock("b2"));
        Assert.assertNotNull(world.getBlock("b2plus"));
        Assert.assertEquals(2, world.getBlockChain().getBestBlockInformation().getBlockNumber());
    }

    @Test
    public void runBlockchain05Resource() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain05.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        Assert.assertNotNull(world.getBlock("b1"));
        Assert.assertNotNull(world.getBlock("b1b"));
        Assert.assertNotNull(world.getBlock("b1c"));
        Assert.assertNotNull(world.getBlock("b2"));
        Assert.assertNotNull(world.getBlock("b2plus"));
        Assert.assertEquals(2, world.getBlockChain().getBestBlockInformation().getBlockNumber());
    }
}

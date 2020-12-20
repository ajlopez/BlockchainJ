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
}

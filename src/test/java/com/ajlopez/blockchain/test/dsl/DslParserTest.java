package com.ajlopez.blockchain.test.dsl;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ajlopez on 11/05/2019.
 */
public class DslParserTest {
    @Test
    public void parseDslCommand() throws IOException {
        DslParser parser = new DslParser(new BufferedReader(new StringReader("account acc1 10000")));
        DslCommand dslCommand = parser.parse();

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }

    @Test
    public void parseDslCommandSkippingSpacesAndTabs() throws IOException {
        DslParser parser = new DslParser(new BufferedReader(new StringReader("  account  \t acc1   10000   ")));
        DslCommand dslCommand = parser.parse();

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }

    @Test
    public void parseDslCommandSkippingComment() throws IOException {
        DslParser parser = new DslParser(new BufferedReader(new StringReader("  account  \t acc1   10000   # a comment")));
        DslCommand dslCommand = parser.parse();

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }
}

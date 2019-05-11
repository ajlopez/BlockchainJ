package com.ajlopez.blockchain.test.dsl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 11/05/2019.
 */
public class DslParserTest {
    @Test
    public void parseDslCommand() {
        DslCommand dslCommand = DslParser.parse("account acc1 10000");

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }

    @Test
    public void parseDslCommandSkippingSpacesAndTabs() {
        DslCommand dslCommand = DslParser.parse("  account  \t acc1   10000   ");

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }

    @Test
    public void parseDslCommandSkippingComment() {
        DslCommand dslCommand = DslParser.parse("  account  \t acc1   10000   # a comment");

        Assert.assertNotNull(dslCommand);
        Assert.assertEquals("account", dslCommand.getVerb());
        Assert.assertEquals(2, dslCommand.getArguments().size());
        Assert.assertEquals("acc1", dslCommand.getArguments().get(0));
        Assert.assertEquals("10000", dslCommand.getArguments().get(1));
    }
}

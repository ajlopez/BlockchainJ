package com.ajlopez.blockchain.test.dsl;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 10/05/2019.
 */
public class DslCommandTest {
    @Test
    public void createCommandWithVerbAndArguments() {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("acc1");
        arguments.add("1000000");

        DslCommand command = new DslCommand("account", arguments);

        Assert.assertEquals(verb, command.getVerb());
        Assert.assertEquals(2, command.getArguments().size());
        Assert.assertEquals("acc1", command.getArguments().get(0));
        Assert.assertEquals("1000000", command.getArguments().get(1));
    }
}

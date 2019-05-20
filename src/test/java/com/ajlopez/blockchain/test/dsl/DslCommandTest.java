package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.test.World;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        Assert.assertNotNull(command.getNamedArgumens());
        Assert.assertTrue(command.getNamedArgumens().isEmpty());
    }

    @Test
    public void createCommandWithVerbAndArgumentsAndNamedArguments() {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("acc1");
        arguments.add("balance=1000000");

        DslCommand command = new DslCommand("account", arguments);

        Assert.assertEquals(verb, command.getVerb());
        Assert.assertEquals(1, command.getArguments().size());
        Assert.assertEquals("acc1", command.getArguments().get(0));

        Map<String, String> namedArguments = command.getNamedArgumens();

        Assert.assertNotNull(namedArguments);
        Assert.assertFalse(namedArguments.isEmpty());
        Assert.assertEquals("1000000", namedArguments.get("balance"));
    }

    @Test
    public void executeAccountCommand() {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("acc1");
        arguments.add("1000000");
        arguments.add("42");

        DslCommand command = new DslCommand("account", arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new BigInteger("1000000"), result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }

    @Test
    public void executeAccountCommandUsingNamedArguments() {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=acc1");
        arguments.add("balance=1000000");
        arguments.add("nonce=42");

        DslCommand command = new DslCommand("account", arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new BigInteger("1000000"), result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }
}

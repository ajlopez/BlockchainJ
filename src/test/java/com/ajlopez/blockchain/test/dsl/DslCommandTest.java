package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 10/05/2019.
 */
public class DslCommandTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

        Assert.assertNotNull(command.getNamedArguments());
        Assert.assertTrue(command.getNamedArguments().isEmpty());
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

        Map<String, String> namedArguments = command.getNamedArguments();

        Assert.assertNotNull(namedArguments);
        Assert.assertFalse(namedArguments.isEmpty());
        Assert.assertEquals("1000000", namedArguments.get("balance"));
    }

    @Test
    public void executeAccountCommand() throws IOException, DslException {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("acc1");
        arguments.add("1000000");
        arguments.add("42");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new Coin(new BigInteger("1000000")), result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }

    @Test
    public void executeAccountCommandWithCode() throws IOException, DslException {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=acc1");
        arguments.add("balance=1000000");
        arguments.add("nonce=42");
        arguments.add("code=01020304");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new Coin(new BigInteger("1000000")), result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertNotNull(result.getCodeHash());

        byte[] code = world.getCode(result.getCodeHash());

        Assert.assertNotNull(code);
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, code);

    }

    @Test
    public void executeAccountCommandUsingNamedArguments() throws IOException, DslException {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=acc1");
        arguments.add("balance=1000000");
        arguments.add("nonce=42");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new Coin(new BigInteger("1000000")), result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }

    @Test
    public void executeAccountCommandUsingNamedArgumentsAndDefaultArguments() throws IOException, DslException {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=acc1");
        arguments.add("balance=1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ONE, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void executeAccountCommandUsingOnlyName() throws IOException, DslException {
        String verb = "account";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=acc1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void executeTransactionCommand() throws IOException, DslException {
        String verb = "transaction";
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();

        List<String> arguments = new ArrayList<>();
        arguments.add("tx1");
        arguments.add(from.toString());
        arguments.add(to.toString());
        arguments.add("10000");
        arguments.add("1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Transaction result = world.getTransaction("tx1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new Coin(new BigInteger("10000")), result.getValue());
        Assert.assertEquals(from, result.getSender());
        Assert.assertEquals(to, result.getReceiver());
        Assert.assertEquals(1, result.getNonce());
    }

    @Test
    public void executeTransactionCommandUsingNamedArguments() throws IOException, DslException {
        String verb = "transaction";
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();

        List<String> arguments = new ArrayList<>();
        arguments.add("name=tx1");
        arguments.add("from=" + from.toString());
        arguments.add("to=" + to.toString());
        arguments.add("value=10000");
        arguments.add("nonce=1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Transaction result = world.getTransaction("tx1");

        Assert.assertNotNull(result);
        Assert.assertEquals(new Coin(new BigInteger("10000")), result.getValue());
        Assert.assertEquals(from, result.getSender());
        Assert.assertEquals(to, result.getReceiver());
        Assert.assertEquals(1, result.getNonce());
    }

    @Test
    public void executeBlockCommand() throws IOException, DslException {
        String verb = "block";
        List<String> arguments = new ArrayList<>();
        arguments.add("blk1");
        arguments.add("genesis");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(world.getBlock("genesis").getHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertTrue(result.getTransactions().isEmpty());
    }

    @Test
    public void executeBlockCommandWithTransactions() throws IOException, DslException {
        String verb = "block";
        List<String> arguments = new ArrayList<>();
        arguments.add("blk1");
        arguments.add("genesis");
        arguments.add("tx1,tx2");

        Transaction transaction1 = FactoryHelper.createTransaction(1000);
        Transaction transaction2 = FactoryHelper.createTransaction(2000);

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        world.setTransaction("tx1", transaction1);
        world.setTransaction("tx2", transaction2);

        command.execute(world);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(world.getBlock("genesis").getHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(2, result.getTransactions().size());
        Assert.assertEquals(transaction1.getHash(), result.getTransactions().get(0).getHash());
        Assert.assertEquals(transaction2.getHash(), result.getTransactions().get(1).getHash());
    }

    @Test
    public void executeBlockCommandUsingDefaultParent() throws IOException, DslException {
        String verb = "block";
        List<String> arguments = new ArrayList<>();
        arguments.add("blk1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(world.getBlock("genesis").getHash(), result.getParentHash());
    }

    @Test
    public void executeBlockCommandUsingNamedArguments() throws IOException, DslException {
        String verb = "block";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=blk1");
        arguments.add("parent=genesis");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(world.getBlock("genesis").getHash(), result.getParentHash());
    }

    @Test
    public void executeBlockCommandUsingNamedArgumentsWithTransactions() throws IOException, DslException {
        String verb = "block";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=blk1");
        arguments.add("parent=genesis");
        arguments.add("transactions=tx1,tx2");

        Transaction transaction1 = FactoryHelper.createTransaction(1000);
        Transaction transaction2 = FactoryHelper.createTransaction(2000);

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        world.setTransaction("tx1", transaction1);
        world.setTransaction("tx2", transaction2);

        command.execute(world);

        Block result = world.getBlock("blk1");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(world.getBlock("genesis").getHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(2, result.getTransactions().size());
        Assert.assertEquals(transaction1.getHash(), result.getTransactions().get(0).getHash());
        Assert.assertEquals(transaction2.getHash(), result.getTransactions().get(1).getHash());
    }

    @Test
    public void executeConnectBlock() throws IOException, DslException {
        World world = new World();
        Block genesis = world.getBlock("genesis");
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        world.setBlock("blk1", block);

        String verb = "connect";
        List<String> arguments = new ArrayList<>();
        arguments.add("blk1");

        DslCommand command = new DslCommand(verb, arguments);

        command.execute(world);

        Block result = world.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void executeConnectBlockUsingNamedArgument() throws IOException, DslException {
        World world = new World();
        Block genesis = world.getBlock("genesis");
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        world.setBlock("blk1", block);

        String verb = "connect";
        List<String> arguments = new ArrayList<>();
        arguments.add("name=blk1");

        DslCommand command = new DslCommand(verb, arguments);

        command.execute(world);

        Block result = world.getBlockChain().getBestBlockInformation().getBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getNumber());
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void unknownVerb() throws IOException, DslException {
        World world = new World();

        String verb = "foo";
        List<String> arguments = new ArrayList<>();
        arguments.add("bar");

        DslCommand command = new DslCommand(verb, arguments);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("unknown verb 'foo'");
        command.execute(world);
    }

    @Test
    public void executeAssertCommandWithTrueConstant() throws IOException, DslException {
        String verb = "assert";
        List<String> arguments = new ArrayList<>();
        arguments.add("true");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);
    }

    @Test
    public void executeAssertCommandWithFalseConstant() throws IOException, DslException {
        String verb = "assert";
        List<String> arguments = new ArrayList<>();
        arguments.add("false");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        exception.expect(DslException.class);
        exception.expectMessage("unsatisfied assertion 'false'");
        command.execute(world);
    }

    @Test
    public void executeAssertCommandWithTrueComparison() throws IOException, DslException {
        String verb = "assert";
        List<String> arguments = new ArrayList<>();
        arguments.add("1");
        arguments.add("==");
        arguments.add("1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        command.execute(world);
    }

    @Test
    public void executeAssertCommandWithFalseComparison() throws IOException, DslException {
        String verb = "assert";
        List<String> arguments = new ArrayList<>();
        arguments.add("1");
        arguments.add("!=");
        arguments.add("1");

        DslCommand command = new DslCommand(verb, arguments);
        World world = new World();

        exception.expect(DslException.class);
        exception.expectMessage("unsatisfied assertion '1 != 1'");
        command.execute(world);
    }
}

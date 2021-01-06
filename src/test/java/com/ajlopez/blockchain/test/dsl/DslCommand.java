package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by ajlopez on 10/05/2019.
 */
public class DslCommand {
    private final String verb;
    private final List<String> arguments = new ArrayList<>();
    private final Map<String, String> namedArguments = new HashMap<>();

    public DslCommand(String verb, List<String> arguments) {
        this.verb = verb;

        for (String argument : arguments) {
            int p = argument.indexOf('=');

            if (p > 0 && p < argument.length() - 1) {
                String key = argument.substring(0, p);
                String value = argument.substring(p + 1);

                this.namedArguments.put(key, value);
            }
            else
                this.arguments.add(argument);
        }
    }

    public String getVerb() {
        return this.verb;
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public Map<String, String> getNamedArguments() { return this.namedArguments; }

    public void execute(World world) throws IOException, DslException {
        if ("account".equals(this.verb))
            executeAccount(world);
        else if ("block".equals(this.verb))
            executeBlock(world);
        else if ("transaction".equals(this.verb))
            executeTransaction(world);
        else if ("connect".equals(this.verb))
            executeConnect(world);
        else if ("process".equals(this.verb))
            executeProcess(world);
        else if ("assert".equals(this.verb))
            executeAssert(world);
        else
            throw new UnsupportedOperationException(String.format("unknown verb '%s'", this.verb));
    }

    private void executeProcess(World world) throws IOException {
        String name = this.getName(0, "name");
        Block block = world.getBlock(name);
        world.getBlockProcessor().processBlock(block);
    }

    private void executeConnect(World world) throws IOException {
        String name = this.getName(0, "name");
        Block block = world.getBlock(name);
        world.getBlockChain().connectBlock(block);
    }

    private void executeAssert(World world) throws IOException, DslException {
        DslExpression expression;

        if (this.arguments.size() == 1)
            expression = toDslExpression(this.arguments.get(0));
        else
            expression = new DslComparison(toDslExpression(this.arguments.get(0)), this.arguments.get(1), toDslExpression(this.arguments.get(2)));

        if (Boolean.FALSE.equals(expression.evaluate(world)))
            throw new DslException(String.format("unsatisfied assertion '%s'", this.argumentsToString()));
    }

    private void executeTransaction(World world) {
        String name = this.getName(0, "name");
        Address from = this.getAddress(world, 1, "from");
        Address to = this.getAddress(world, 2, "to");
        Coin value = this.getCoin(3, "value");
        long nonce = this.getLongInteger(4, "nonce");

        Transaction transaction = new Transaction(from, to, value, nonce, null, 6000000, Coin.ZERO);

        world.setTransaction(name, transaction);
    }

    private void executeBlock(World world) throws IOException {
        String name = this.getName(0, "name");
        String parentName = this.getName(1, "parent");

        if (parentName == null)
            parentName = "genesis";

        List<String> transactionNames = this.getNames(2, "transactions");

        Block parent = world.getBlock(parentName);
        List<Transaction> transactions = world.getTransactions(transactionNames);
        Block block = FactoryHelper.createBlock(parent, FactoryHelper.createRandomAddress(), transactions);

        world.setBlock(name, block);
    }

    private void executeAccount(World world) throws IOException {
        String name = this.getName(0, "name");
        Coin balance = this.getCoin(1, "balance");
        long nonce = this.getLongInteger(2, "nonce");
        String bytecodes = this.getName(3, "code");
        Hash codeHash = null;
        byte[] code = null;

        if (bytecodes != null) {
            code = HexUtils.hexStringToBytes(bytecodes);
            codeHash = HashUtils.calculateHash(code);
            world.setCode(codeHash, code);
        }

        Account account = new Account(balance, nonce, code != null ? code.length : 0, codeHash, null);

        world.setAccount(name, account);
    }

    private String getName(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return this.arguments.get(position);

        return this.namedArguments.get(name);
    }

    private Address getAddress(World world, int position, String name) {
        String argument = this.getName(position, name);

        if (argument.startsWith("0x")  || argument.startsWith("0X"))
            return new Address(HexUtils.hexStringToBytes(argument));

        return world.getAccountAddress(argument);
    }

    private List<String> getNames(int position, String name) {
        String text = this.getName(position, name);

        if (text == null)
            return Collections.emptyList();

        String[] names = text.split(",");

        List<String> result = new ArrayList<>();

        for (int k = 0; k < names.length; k++)
            result.add(names[k]);

        return result;
    }

    private Coin getCoin(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return new Coin(new BigInteger(this.arguments.get(position)));

        String value = this.namedArguments.get(name);

        if (value == null)
            return Coin.ZERO;

        return new Coin(new BigInteger(value));
    }

    private long getLongInteger(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return Long.parseLong(this.arguments.get(position));

        String value = this.namedArguments.get(name);

        if (value == null)
            return 0L;

        return Long.parseLong(value);
    }

    private String argumentsToString() {
        String result = "";

        for (String argument : this.arguments)
            if (result.length() > 0)
                result += " " + argument;
            else
                result = argument;

        return result;
    }

    private static DslExpression toDslExpression(String text) {
        int p = text.lastIndexOf('.');

        if (p > 0)
            return new DslDotExpression(toDslExpression(text.substring(0, p)), text.substring(p + 1));

        return new DslTerm(text);
    }
}


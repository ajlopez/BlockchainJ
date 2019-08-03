package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HexUtils;

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

            if (p > 0) {
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

    public void execute(World world) {
        if ("account".equals(this.verb)) {
            String name = this.getName(0, "name");
            BigInteger balance = this.getBigInteger(1, "balance");
            long nonce = this.getLongInteger(2, "nonce");

            Account account = new Account(balance, nonce, null, null);

            world.setAccount(name, account);

            return;
        }

        if ("block".equals(this.verb)) {
            String name = this.getName(0, "name");
            String parentName = this.getName(1, "parent");

            if (parentName == null)
                parentName = "genesis";

            List<String> transactionNames = this.getNames(2, "transactions");

            Block parent = world.getBlock(parentName);
            List<Transaction> transactions = world.getTransactions(transactionNames);
            Block block = FactoryHelper.createBlock(parent, FactoryHelper.createRandomAddress(), transactions);

            world.setBlock(name, block);

            return;
        }

        if ("transaction".equals(this.verb)) {
            String name = this.getName(0, "name");
            Address from = new Address(HexUtils.hexStringToBytes(this.getName(1, "from")));
            Address to = new Address(HexUtils.hexStringToBytes(this.getName(2, "to")));
            BigInteger value = this.getBigInteger(3, "value");
            long nonce = this.getLongInteger(4, "nonce");

            Transaction transaction = new Transaction(from, to, value, nonce, null, 6000000, BigInteger.ZERO);

            world.setTransaction(name, transaction);

            return;
        }

        if ("connect".equals(this.verb)) {
            String name = this.getName(0, "name");
            Block block = world.getBlock(name);
            world.getBlockChain().connectBlock(block);
        }
    }

    private String getName(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return this.arguments.get(position);

        return this.namedArguments.get(name);
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

    private BigInteger getBigInteger(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return new BigInteger(this.arguments.get(position));

        String value = this.namedArguments.get(name);

        if (value == null)
            return BigInteger.ZERO;

        return new BigInteger(value);
    }

    private long getLongInteger(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return Long.parseLong(this.arguments.get(position));

        String value = this.namedArguments.get(name);

        if (value == null)
            return 0L;

        return Long.parseLong(value);
    }
}


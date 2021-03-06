package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.commands.*;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by ajlopez on 10/05/2019.
 */
public abstract class DslCommand {
    private final String verb;
    private final List<String> arguments = new ArrayList<>();
    private final Map<String, String> namedArguments = new HashMap<>();

    public static DslCommand createCommand(String verb, List<String> arguments) {
        if ("account".equals(verb))
            return new DslAccountCommand(arguments);

        if ("block".equals(verb))
            return new DslBlockCommand(arguments);

        if ("transaction".equals(verb))
            return new DslTransactionCommand(arguments);

        if ("header".equals(verb))
            return new DslBlockHeaderCommand(arguments);

        if ("connect".equals(verb))
            return new DslConnectCommand(arguments);

        if ("process".equals(verb))
            return new DslProcessCommand(arguments);

        if ("assert".equals(verb))
            return new DslAssertCommand(arguments);

        throw new UnsupportedOperationException(String.format("unknown verb '%s'", verb));
    }

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

    public abstract void execute(World world) throws IOException, DslException;

    public String getName(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return this.arguments.get(position);

        return this.namedArguments.get(name);
    }

    public Address getAddress(World world, int position, String name) {
        String argument = this.getName(position, name);

        if (argument.startsWith("0x")  || argument.startsWith("0X"))
            return new Address(HexUtils.hexStringToBytes(argument));

        return world.getAccountAddress(argument);
    }

    public List<String> getNames(int position, String name) {
        String text = this.getName(position, name);

        if (text == null)
            return Collections.emptyList();

        String[] names = text.split(",");

        List<String> result = new ArrayList<>();

        for (int k = 0; k < names.length; k++)
            result.add(names[k]);

        return result;
    }

    public Coin getCoin(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return new Coin(new BigInteger(this.arguments.get(position)));

        String value = this.namedArguments.get(name);

        if (value == null)
            return Coin.ZERO;

        return new Coin(new BigInteger(value));
    }

    public long getLongInteger(int position, String name) {
        if (position >= 0 && this.arguments.size() > position)
            return Long.parseLong(this.arguments.get(position));

        String value = this.namedArguments.get(name);

        if (value == null)
            return 0L;

        return Long.parseLong(value);
    }
}


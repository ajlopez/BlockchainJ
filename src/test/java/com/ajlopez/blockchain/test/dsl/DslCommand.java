package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.test.World;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 10/05/2019.
 */
public class DslCommand {
    private final String verb;
    private final List<String> arguments = new ArrayList<>();
    private final Map<String, String> namedArgumens = new HashMap<>();

    public DslCommand(String verb, List<String> arguments) {
        this.verb = verb;

        for (String argument : arguments) {
            int p = argument.indexOf('=');

            if (p > 0) {
                String key = argument.substring(0, p);
                String value = argument.substring(p + 1);

                this.namedArgumens.put(key, value);
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

    public Map<String, String> getNamedArgumens() { return this.namedArgumens; }

    public void execute(World world) {
        if ("account".equals(this.verb)) {
            String name = this.arguments.get(0);
            BigInteger balance = new BigInteger(this.arguments.get(1));
            long nonce = Long.parseLong(this.arguments.get(2));

            Account account = new Account(balance, nonce, null, null);

            world.setAccount(name, account);
        }
    }
}


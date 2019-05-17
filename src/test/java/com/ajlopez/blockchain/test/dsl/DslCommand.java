package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.test.World;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by ajlopez on 10/05/2019.
 */
public class DslCommand {
    private final String verb;
    private final List<String> arguments;

    public DslCommand(String verb, List<String> arguments) {
        this.verb = verb;
        this.arguments = arguments;
    }

    public String getVerb() {
        return this.verb;
    }

    public List<String> getArguments() {
        return this.arguments;
    }

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


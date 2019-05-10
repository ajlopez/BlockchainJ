package com.ajlopez.blockchain.test.dsl;

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
}


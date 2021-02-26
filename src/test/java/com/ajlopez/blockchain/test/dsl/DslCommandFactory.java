package com.ajlopez.blockchain.test.dsl;

import java.util.List;

/**
 * Created by ajlopez on 26/02/2021.
 */
public class DslCommandFactory {
    public DslCommand createCommand(String verb, List<String> arguments) {
        return new DslCommand(verb, arguments);
    }
}

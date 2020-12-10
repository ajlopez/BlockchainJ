package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 08/12/2020.
 */
public class DslTerm {
    private final String term;

    public DslTerm(String term) {
        this.term = term;
    }

    public Object evaluate(World world) throws IOException {
        if (this.term.equals("blockchain"))
            return world.getBlockChain();

        Object result = world.getAccount(this.term);

        if (result != null)
            return result;

        return world.getBlock(this.term);
    }
}

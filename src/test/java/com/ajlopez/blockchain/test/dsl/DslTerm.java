package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 08/12/2020.
 */
public class DslTerm implements DslExpression {
    private final String term;

    public DslTerm(String term) {
        this.term = term;
    }

    @Override
    public Object evaluate(World world) throws IOException {
        if (Character.isDigit(this.term.charAt(0)))
            return Integer.parseInt(this.term);

        if ("true".equals(this.term))
            return true;

        if ("false".equals(this.term))
            return false;

        if (this.term.equals("blockchain"))
            return world.getBlockChain();

        Object result = world.getAccount(this.term);

        if (result != null)
            return result;

        result = world.getTransaction(this.term);

        if (result != null)
            return result;

        return world.getBlock(this.term);
    }
}

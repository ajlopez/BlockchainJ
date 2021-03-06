package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 11/12/2020.
 */
public class DslDotExpression implements DslExpression {
    private final DslExpression expression;
    private final String name;

    public DslDotExpression(DslExpression expression, String name) {
        this.expression = expression;
        this.name = name;
    }

    @Override
    public Object evaluate(World world) throws IOException {
        Object leftValue = this.expression.evaluate(world);

        if ("balance".equals(this.name))
            return world.getAccount((Address)leftValue).getBalance();

        if ("nonce".equals(this.name))
            if (leftValue instanceof Transaction)
                return ((Transaction)leftValue).getNonce();
            else
                return world.getAccount((Address)leftValue).getNonce();

        if ("value".equals(this.name))
            return ((Transaction)leftValue).getValue();

        if ("from".equals(this.name))
            return ((Transaction)leftValue).getSender();

        if ("to".equals(this.name))
            return ((Transaction)leftValue).getReceiver();

        if ("number".equals(this.name))
            return ((Block)leftValue).getNumber();

        if ("hash".equals(this.name))
            return ((Block)leftValue).getHash();

        if ("bestBlock".equals(this.name))
            return ((BlockChain)leftValue).getBestBlockInformation().getBlock();

        // TODO exception case?
        return null;
    }
}

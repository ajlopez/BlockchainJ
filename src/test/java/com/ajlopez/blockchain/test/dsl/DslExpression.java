package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 11/12/2020.
 */
public interface DslExpression {
    Object evaluate(World world) throws IOException;
}

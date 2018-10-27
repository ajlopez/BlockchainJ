package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class TokenTest {
    @Test
    public void createToken() {
        Token token = new Token(TokenType.NAME, "adam");

        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("adam", token.getValue());
    }
}

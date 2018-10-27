package com.ajlopez.blockchain.json;

import java.io.Reader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class Parser {
    private Lexer lexer;

    public Parser(Reader reader) {
        this.lexer = new Lexer(reader);
    }

    public Value parseValue() {
        return null;
    }
}

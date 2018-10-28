package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class Parser {
    private Lexer lexer;

    public Parser(Reader reader) {
        this.lexer = new Lexer(reader);
    }

    public Value parseValue() throws IOException, LexerException, ParserException {
        Token token = this.lexer.nextToken();

        if (token == null)
            return null;

        if (token.getType() == TokenType.NAME) {
            if (token.getValue().equals("true"))
                return new BooleanValue(true);
            if (token.getValue().equals("false"))
                return new BooleanValue(false);

            throw new ParserException(String.format("Invalid value '%s'", token.getValue()));
        }

        if (token.getType() == TokenType.STRING)
            return new StringValue(token.getValue());

        return new NumericValue(token.getValue());
    }
}

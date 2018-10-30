package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

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
        }
        else if (token.getType() == TokenType.STRING)
            return new StringValue(token.getValue());
        else if (token.getType() == TokenType.NUMBER)
            return new NumericValue(token.getValue());
        else if (token.getType() == TokenType.SYMBOL) {
            if (token.getValue().equals("{"))
                return this.parseObjectValue();
        }

        throw new ParserException(String.format("Invalid value '%s'", token.getValue()));
    }

    private ObjectValue parseObjectValue() throws IOException, LexerException, ParserException {
        Map<String, Value> properties = new LinkedHashMap<>();

        while (!this.tryParseSymbol("}")) {
            String name = this.parseString();

            if (!this.tryParseSymbol(":"))
                throw new ParserException("Expected ':'");

            Value value = this.parseValue();

            properties.put(name, value);

            if (this.tryParseSymbol(","))
                continue;

            if (!this.tryParseSymbol("}"))
                throw new ParserException("Unclosed object");

            break;
        }

        return new ObjectValue(properties);
    }

    private String parseString() throws ParserException, IOException, LexerException {
        Token token = this.lexer.nextToken();

        if (token == null || token.getType() != TokenType.STRING)
            throw new ParserException ("Expected string");

        return token.getValue();
    }

    private boolean tryParseSymbol(String symbol) throws IOException, LexerException {
        Token token = this.lexer.nextToken();

        if (token == null)
            return false;

        if (token.getType() == TokenType.SYMBOL && token.getValue().equals(symbol))
            return true;

        this.lexer.pushToken(token);

        return false;
    }
}

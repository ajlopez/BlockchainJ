package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonParser {
    private final JsonLexer lexer;

    public JsonParser(Reader reader) {
        this.lexer = new JsonLexer(reader);
    }

    public JsonValue parseValue() throws IOException, JsonLexerException, JsonParserException {
        Token token = this.lexer.nextToken();

        if (token == null)
            return null;

        if (token.getType() == TokenType.NAME) {
            if (token.getValue().equals("true"))
                return new JsonBooleanValue(true);
            if (token.getValue().equals("false"))
                return new JsonBooleanValue(false);
        }
        else if (token.getType() == TokenType.STRING)
            return new JsonStringValue(token.getValue());
        else if (token.getType() == TokenType.NUMBER)
            return new JsonNumericValue(token.getValue());
        else if (token.getType() == TokenType.SYMBOL) {
            if (token.getValue().equals("{"))
                return this.parseObjectValue();
            if (token.getValue().equals("["))
                return this.parseArrayValue();
        }

        throw new JsonParserException(String.format("Invalid value '%s'", token.getValue()));
    }

    private JsonObjectValue parseObjectValue() throws IOException, JsonLexerException, JsonParserException {
        Map<String, JsonValue> properties = new LinkedHashMap<>();

        while (!this.tryParseSymbol("}")) {
            String name = this.parseString();

            if (!this.tryParseSymbol(":"))
                throw new JsonParserException("Expected ':'");

            JsonValue value = this.parseValue();

            properties.put(name, value);

            if (this.tryParseSymbol(","))
                continue;

            if (!this.tryParseSymbol("}"))
                throw new JsonParserException("Unclosed object");

            break;
        }

        return new JsonObjectValue(properties);
    }

    private JsonArrayValue parseArrayValue() throws IOException, JsonLexerException, JsonParserException {
        List<JsonValue> elements = new ArrayList<>();

        if (!this.tryParseSymbol("]"))
            while (true) {
                JsonValue value = this.parseValue();

                elements.add(value);

                if (this.tryParseSymbol(","))
                    continue;

                if (!this.tryParseSymbol("]"))
                    throw new JsonParserException("Unclosed array");

                break;
            }

        return new JsonArrayValue(elements);
    }

    private String parseString() throws JsonParserException, IOException, JsonLexerException {
        Token token = this.lexer.nextToken();

        if (token == null || token.getType() != TokenType.STRING)
            throw new JsonParserException("Expected string");

        return token.getValue();
    }

    private boolean tryParseSymbol(String symbol) throws IOException, JsonLexerException {
        Token token = this.lexer.nextToken();

        if (token == null)
            return false;

        if (token.getType() == TokenType.SYMBOL && token.getValue().equals(symbol))
            return true;

        this.lexer.pushToken(token);

        return false;
    }
}

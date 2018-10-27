package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class LexerTest {
    @Test
    public void processEmptyString() throws IOException {
        Lexer lexer = createLexer("");

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processBlankString() throws IOException {
        Lexer lexer = createLexer("   ");

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processBlankStringWithNewLineCarriageReturn() throws IOException {
        Lexer lexer = createLexer(" \r\n  ");

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleName() throws IOException {
        Lexer lexer = createLexer("adam");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("adam", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNameWithWhitespaces() throws IOException {
        Lexer lexer = createLexer("  adam   ");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("adam", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNameWithInitialUnderscore() throws IOException {
        Lexer lexer = createLexer("_name");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("_name", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNameWithFinalUnderscore() throws IOException {
        Lexer lexer = createLexer("name_");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("name_", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNameWithManyUnderscores() throws IOException {
        Lexer lexer = createLexer("_first_name_");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("_first_name_", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNameWithDigits() throws IOException {
        Lexer lexer = createLexer("foo42");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("foo42", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNumber() throws IOException {
        Lexer lexer = createLexer("42");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NUMBER, token.getType());
        Assert.assertEquals("42", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSimpleNumberWithWhitespaces() throws IOException {
        Lexer lexer = createLexer("   42   ");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NUMBER, token.getType());
        Assert.assertEquals("42", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processSymbol() throws IOException {
        Lexer lexer = createLexer(";");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.SYMBOL, token.getType());
        Assert.assertEquals(";", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processNameAndSymbol() throws IOException {
        Lexer lexer = createLexer("name;");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NAME, token.getType());
        Assert.assertEquals("name", token.getValue());

        token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.SYMBOL, token.getType());
        Assert.assertEquals(";", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    @Test
    public void processNumberAndSymbol() throws IOException {
        Lexer lexer = createLexer("42;");

        Token token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.NUMBER, token.getType());
        Assert.assertEquals("42", token.getValue());

        token = lexer.nextToken();

        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.SYMBOL, token.getType());
        Assert.assertEquals(";", token.getValue());

        Assert.assertNull(lexer.nextToken());
    }

    private static Lexer createLexer(String text) {
        return new Lexer(new StringReader(text));
    }
}

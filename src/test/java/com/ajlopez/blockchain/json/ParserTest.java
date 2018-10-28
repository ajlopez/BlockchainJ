package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ParserTest {
    @Test
    public void parseEmptyStringAsNull() throws IOException, LexerException, ParserException {
        Parser parser = createParser("");

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseIntegerAsNumericValue() throws IOException, LexerException, ParserException {
        Parser parser = createParser("42");

        Value result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseStringAsStringValue() throws IOException, LexerException, ParserException {
        Parser parser = createParser("\"foo\"");

        Value result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseTrueAsBooleanValue() throws IOException, LexerException, ParserException {
        Parser parser = createParser("true");

        Value result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.BOOLEAN, result.getType());
        Assert.assertEquals(true, result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseFalseAsBooleanValue() throws IOException, LexerException, ParserException {
        Parser parser = createParser("false");

        Value result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.BOOLEAN, result.getType());
        Assert.assertEquals(false, result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseInvalidName() throws IOException, LexerException, ParserException {
        Parser parser = createParser("foo");

        try {
            parser.parseValue();
            Assert.fail();
        }
        catch (ParserException ex) {
            Assert.assertEquals("Invalid value 'foo'", ex.getMessage());
        }
    }

    private static Parser createParser(String text) {
        return new Parser(new StringReader(text));
    }
}

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
    public void parseEmptyStringAsNull() throws IOException, LexerException {
        Parser parser = createParser("");

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseIntegerAsNumericValue() throws IOException, LexerException {
        Parser parser = createParser("42");

        Value result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    private static Parser createParser(String text) {
        return new Parser(new StringReader(text));
    }
}

package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class ParserTest {
    @Test
    public void parseEmptyStringAsNull() {
        Parser parser = createParser("");

        Assert.assertNull(parser.parseValue());
    }

    private static Parser createParser(String text) {
        return new Parser(new StringReader(text));
    }
}

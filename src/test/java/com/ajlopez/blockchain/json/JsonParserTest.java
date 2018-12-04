package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonParserTest {
    @Test
    public void parseEmptyStringAsNull() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("");

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseIntegerAsNumericValue() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("42");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseStringAsStringValue() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("\"foo\"");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseTrueAsBooleanValue() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("true");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.BOOLEAN, result.getType());
        Assert.assertEquals(true, result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseFalseAsBooleanValue() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("false");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.BOOLEAN, result.getType());
        Assert.assertEquals(false, result.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseInvalidName() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("foo");

        try {
            parser.parseValue();
            Assert.fail();
        }
        catch (JsonParserException ex) {
            Assert.assertEquals("Invalid value 'foo'", ex.getMessage());
        }
    }

    @Test
    public void parseEmptyObject() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("{}");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(0, oresult.noProperties());
    }

    @Test
    public void parseUnclosedObject() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("{");

        try {
            parser.parseValue();
            Assert.fail();
        }
        catch (JsonParserException ex) {
            Assert.assertEquals("Expected string", ex.getMessage());
        }
    }

    @Test
    public void parseObjectWithTwoProperties() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("{ \"name\": \"adam\", \"age\": 900 }");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(2, oresult.noProperties());

        Assert.assertTrue(oresult.hasProperty("name"));
        JsonValue pname = oresult.getProperty("name");
        Assert.assertNotNull(pname);
        Assert.assertEquals(JsonValueType.STRING, pname.getType());
        Assert.assertEquals("adam", pname.getValue());

        Assert.assertTrue(oresult.hasProperty("age"));
        JsonValue page = oresult.getProperty("age");
        Assert.assertNotNull(page);
        Assert.assertEquals(JsonValueType.NUMBER, page.getType());
        Assert.assertEquals("900", page.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseArrayWithTwoElements() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("[ \"adam\", 900 ]");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(2, aresult.size());

        JsonValue pname = aresult.getValue(0);
        Assert.assertNotNull(pname);
        Assert.assertEquals(JsonValueType.STRING, pname.getType());
        Assert.assertEquals("adam", pname.getValue());

        JsonValue page = aresult.getValue(1);
        Assert.assertNotNull(page);
        Assert.assertEquals(JsonValueType.NUMBER, page.getType());
        Assert.assertEquals("900", page.getValue());

        Assert.assertNull(parser.parseValue());
    }

    @Test
    public void parseObjectWithNestedObject() throws IOException, JsonLexerException, JsonParserException {
        JsonParser parser = createParser("{ \"name\": \"adam\", \"age\": 900, \"wife\": { \"name\": \"eve\", \"age\": 800 } }");

        JsonValue result = parser.parseValue();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(3, oresult.noProperties());

        Assert.assertTrue(oresult.hasProperty("name"));
        JsonValue pname = oresult.getProperty("name");
        Assert.assertNotNull(pname);
        Assert.assertEquals(JsonValueType.STRING, pname.getType());
        Assert.assertEquals("adam", pname.getValue());

        Assert.assertTrue(oresult.hasProperty("age"));
        JsonValue page = oresult.getProperty("age");
        Assert.assertNotNull(page);
        Assert.assertEquals(JsonValueType.NUMBER, page.getType());
        Assert.assertEquals("900", page.getValue());

        JsonValue pname2 = oresult.getProperty("wife","name");
        Assert.assertNotNull(pname2);
        Assert.assertEquals(JsonValueType.STRING, pname2.getType());
        Assert.assertEquals("eve", pname2.getValue());

        JsonValue page2 = oresult.getProperty("wife","age");
        Assert.assertNotNull(page2);
        Assert.assertEquals(JsonValueType.NUMBER, page2.getType());
        Assert.assertEquals("800", page2.getValue());

        Assert.assertNull(parser.parseValue());
    }

    private static JsonParser createParser(String text) {
        return new JsonParser(new StringReader(text));
    }
}

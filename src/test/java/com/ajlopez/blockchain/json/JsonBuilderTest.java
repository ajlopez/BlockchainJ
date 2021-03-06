package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 02/11/2018.
 */
public class JsonBuilderTest {
    @Test
    public void buildStringValue() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value("foo").build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());
    }

    @Test
    public void buildNullValueFromObject() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value((Object)null).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.NULL, result.getType());
        Assert.assertEquals(null, result.getValue());
    }

    @Test
    public void buildNullValueFromString() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value((String)null).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.NULL, result.getType());
        Assert.assertEquals(null, result.getValue());
    }

    @Test
    public void buildNumericValueFromInteger() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());
    }

    @Test
    public void buildBooleanValue() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(true).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.BOOLEAN, result.getType());
        Assert.assertEquals(true, result.getValue());
    }

    @Test
    public void buildArrayWithTwoStringElements() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.array()
                .value("foo")
                .value("bar")
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(2, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(JsonValueType.STRING, aresult.getValue(0).getType());
        Assert.assertEquals("foo", ((JsonStringValue)aresult.getValue(0)).getValue());

        Assert.assertNotNull(aresult.getValue(1));
        Assert.assertEquals(JsonValueType.STRING, aresult.getValue(1).getType());
        Assert.assertEquals("bar", ((JsonStringValue)aresult.getValue(1)).getValue());
    }

    @Test
    public void buildArrayWithNumericElement() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.array()
                .value(42)
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(1, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(JsonValueType.NUMBER, aresult.getValue(0).getType());
        Assert.assertEquals("42", ((JsonNumericValue)aresult.getValue(0)).getValue());
    }

    @Test
    public void buildArrayWithBooleanElement() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.array()
                .value(true)
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(1, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(JsonValueType.BOOLEAN, aresult.getValue(0).getType());
        Assert.assertEquals(true, ((JsonBooleanValue)aresult.getValue(0)).getValue());
    }

    @Test
    public void buildObjectWithTwoProperties() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.object()
                .name("name")
                .value("adam")
                .name("age")
                .value(900)
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(2, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("name"));
        Assert.assertEquals(JsonValueType.STRING, oresult.getProperty("name").getType());
        Assert.assertEquals("adam", ((JsonStringValue)oresult.getProperty("name")).getValue());

        Assert.assertNotNull(oresult.getProperty("age"));
        Assert.assertEquals(JsonValueType.NUMBER, oresult.getProperty("age").getType());
        Assert.assertEquals("900", ((JsonNumericValue)oresult.getProperty("age")).getValue());
    }

    @Test
    public void buildObjectWithNumericElement() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.object()
                .name("answer")
                .value(42)
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(1, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("answer"));
        Assert.assertEquals(JsonValueType.NUMBER, oresult.getProperty("answer").getType());
        Assert.assertEquals("42", ((JsonNumericValue)oresult.getProperty("answer")).getValue());
    }

    @Test
    public void buildObjectWithBooleanElement() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.object()
                .name("alive")
                .value(true)
                .end()
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(1, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("alive"));
        Assert.assertEquals(JsonValueType.BOOLEAN, oresult.getProperty("alive").getType());
        Assert.assertEquals(true, ((JsonBooleanValue)oresult.getProperty("alive")).getValue());
    }
}

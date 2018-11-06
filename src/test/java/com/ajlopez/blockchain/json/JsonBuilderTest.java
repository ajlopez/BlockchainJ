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
        Assert.assertEquals(ValueType.STRING, result.getType());
        Assert.assertEquals("foo", result.getValue());
    }

    @Test
    public void buildNumericValueFromInteger() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.NUMBER, result.getType());
        Assert.assertEquals("42", result.getValue());
    }

    @Test
    public void buildBooleanValue() {
        JsonBuilder builder = new JsonBuilder();

        JsonValue result = builder.value(true).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(ValueType.BOOLEAN, result.getType());
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
        Assert.assertEquals(ValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(2, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(ValueType.STRING, aresult.getValue(0).getType());
        Assert.assertEquals("foo", ((JsonStringValue)aresult.getValue(0)).getValue());

        Assert.assertNotNull(aresult.getValue(1));
        Assert.assertEquals(ValueType.STRING, aresult.getValue(1).getType());
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
        Assert.assertEquals(ValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(1, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(ValueType.NUMBER, aresult.getValue(0).getType());
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
        Assert.assertEquals(ValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(1, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(ValueType.BOOLEAN, aresult.getValue(0).getType());
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
        Assert.assertEquals(ValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(2, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("name"));
        Assert.assertEquals(ValueType.STRING, oresult.getProperty("name").getType());
        Assert.assertEquals("adam", ((JsonStringValue)oresult.getProperty("name")).getValue());

        Assert.assertNotNull(oresult.getProperty("age"));
        Assert.assertEquals(ValueType.NUMBER, oresult.getProperty("age").getType());
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
        Assert.assertEquals(ValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(1, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("answer"));
        Assert.assertEquals(ValueType.NUMBER, oresult.getProperty("answer").getType());
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
        Assert.assertEquals(ValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertEquals(1, oresult.noProperties());

        Assert.assertNotNull(oresult.getProperty("alive"));
        Assert.assertEquals(ValueType.BOOLEAN, oresult.getProperty("alive").getType());
        Assert.assertEquals(true, ((JsonBooleanValue)oresult.getProperty("alive")).getValue());
    }
}

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

        ArrayValue aresult = (ArrayValue)result;

        Assert.assertEquals(2, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(ValueType.STRING, aresult.getValue(0).getType());
        Assert.assertEquals("foo", ((StringValue)aresult.getValue(0)).getValue());

        Assert.assertNotNull(aresult.getValue(1));
        Assert.assertEquals(ValueType.STRING, aresult.getValue(1).getType());
        Assert.assertEquals("bar", ((StringValue)aresult.getValue(1)).getValue());
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

        ArrayValue aresult = (ArrayValue)result;

        Assert.assertEquals(1, aresult.size());

        Assert.assertNotNull(aresult.getValue(0));
        Assert.assertEquals(ValueType.NUMBER, aresult.getValue(0).getType());
        Assert.assertEquals("42", ((NumericValue)aresult.getValue(0)).getValue());
    }
}

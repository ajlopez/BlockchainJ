package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 29/10/2018.
 */
public class JsonArrayValueTest {
    @Test
    public void createArrayValueWithTwoElements() {
        JsonValue value1 = new JsonStringValue("foo");
        JsonValue value2 = new JsonNumericValue("42");
        List<JsonValue> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        JsonArrayValue value = new JsonArrayValue(values);

        Assert.assertEquals(2, value.size());
        Assert.assertSame(value1, value.getValue(0));
        Assert.assertSame(value2, value.getValue(1));
    }

    @Test
    public void arrayValueWithTwoElementsToString() {
        JsonValue value1 = new JsonStringValue("foo");
        JsonValue value2 = new JsonNumericValue("42");
        List<JsonValue> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        JsonArrayValue value = new JsonArrayValue(values);

        Assert.assertEquals("[ \"foo\", 42 ]", value.toString());
    }

    @Test
    public void arrayValueWithNoElementsToString() {
        List<JsonValue> values = new ArrayList<>();

        JsonArrayValue value = new JsonArrayValue(values);

        Assert.assertEquals("[]", value.toString());
    }
}


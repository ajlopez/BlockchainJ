package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 29/10/2018.
 */
public class ArrayValueTest {
    @Test
    public void createArrayValueWithTwoElements() {
        Value value1 = new StringValue("foo");
        Value value2 = new NumericValue("42");
        List<Value> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        ArrayValue value = new ArrayValue(values);

        Assert.assertEquals(2, value.size());
        Assert.assertSame(value1, value.getValue(0));
        Assert.assertSame(value2, value.getValue(1));
    }

    @Test
    public void arrayValueWithTwoElementsToString() {
        Value value1 = new StringValue("foo");
        Value value2 = new NumericValue("42");
        List<Value> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        ArrayValue value = new ArrayValue(values);

        Assert.assertEquals("[ \"foo\", 42 ]", value.toString());
    }

    @Test
    public void arrayValueWithNoElementsToString() {
        List<Value> values = new ArrayList<>();

        ArrayValue value = new ArrayValue(values);

        Assert.assertEquals("[]", value.toString());
    }
}


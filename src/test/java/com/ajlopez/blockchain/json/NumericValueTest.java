package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class NumericValueTest {
    @Test
    public void createNumericValue() {
        NumericValue value = new NumericValue("42");

        Assert.assertEquals(ValueType.NUMBER, value.getType());
        Assert.assertEquals("42", value.getValue());
    }
}


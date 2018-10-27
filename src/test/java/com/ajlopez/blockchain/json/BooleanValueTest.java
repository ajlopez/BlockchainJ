package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class BooleanValueTest {
    @Test
    public void createFalseBooleanValue() {
        BooleanValue value = new BooleanValue(false);

        Assert.assertEquals(ValueType.BOOLEAN, value.getType());
        Assert.assertEquals(false, value.getValue());
    }

    @Test
    public void createTrueBooleanValue() {
        BooleanValue value = new BooleanValue(true);

        Assert.assertEquals(ValueType.BOOLEAN, value.getType());
        Assert.assertEquals(true, value.getValue());
    }
}


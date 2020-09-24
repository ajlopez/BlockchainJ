package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/09/2020.
 */
public class FieldTest {
    @Test
    public void createField() {
        Field field = new Field(BigInteger.valueOf(7));

        Assert.assertEquals(BigInteger.valueOf(7), field.getPrime());
    }
}

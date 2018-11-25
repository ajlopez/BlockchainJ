package com.ajlopez.blockchain.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class ArgumentsProcessorTest {
    @Test
    public void getStringNullIfUndefinedName() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        String result = processor.getString("peer");

        Assert.assertNull(result);
    }

    @Test
    public void defineStringAndGetDefaultValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineString("p", "peer", "localhost:3000");

        String result = processor.getString("peer");

        Assert.assertNotNull(result);
        Assert.assertEquals("localhost:3000", result);
    }
}

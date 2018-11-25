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

    @Test
    public void defineStringProcessArgumentsAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineString("p", "peer", "localhost:3000");

        processor.processArguments(new String[] { "--peer", "localhost:4000" });

        String result = processor.getString("peer");

        Assert.assertNotNull(result);
        Assert.assertEquals("localhost:4000", result);
    }

    @Test
    public void defineStringProcessArgumentWithShortNameAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineString("p", "peer", "localhost:3000");

        processor.processArguments(new String[] { "-p", "localhost:4000" });

        String result = processor.getString("peer");

        Assert.assertNotNull(result);
        Assert.assertEquals("localhost:4000", result);
    }

    @Test
    public void defineIntegerAndGetDefaultValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 3000);

        int result = processor.getInteger("port");

        Assert.assertEquals(3000, result);
    }

    @Test
    public void defineIntegerProcessArgumentsAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 3000);

        processor.processArguments(new String[] { "--port", "4000" });

        int result = processor.getInteger("port");

        Assert.assertEquals(4000, result);
    }

    @Test
    public void defineIntegerProcessArgumentWithShortNameAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 3000);

        processor.processArguments(new String[] { "-p", "4000" });

        int result = processor.getInteger("port");

        Assert.assertEquals(4000, result);
    }
}

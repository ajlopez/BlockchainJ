package com.ajlopez.blockchain.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void defineBooleanAndGetDefaultValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineBoolean("m", "miner", false);

        boolean result = processor.getBoolean("miner");

        Assert.assertEquals(false, result);
    }

    @Test
    public void defineBooleanProcessArgumentsAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineBoolean("m", "miner", false);

        processor.processArguments(new String[] { "--miner" });

        boolean result = processor.getBoolean("miner");

        Assert.assertEquals(true, result);
    }

    @Test
    public void defineBooleanProcessArgumentWithShortNameAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineBoolean("m", "miner", false);

        processor.processArguments(new String[] { "-m" });

        boolean result = processor.getBoolean("miner");

        Assert.assertEquals(true, result);
    }

    @Test
    public void defineStringListAndGetDefaultValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineStringList("p", "peers", "localhost:3000,localhost:3001");

        List<String> result  = processor.getStringList("peers");

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("localhost:3000", result.get(0));
        Assert.assertEquals("localhost:3001", result.get(1));
    }

    @Test
    public void defineStringListProcessArgumentsAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineStringList("p", "peers", "localhost:3000,localhost:3001");

        processor.processArguments(new String[] { "--peers", "localhost:1000,localhost:1001,localhost:1002" });

        List<String> result = processor.getStringList("peers");

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("localhost:1000", result.get(0));
        Assert.assertEquals("localhost:1001", result.get(1));
        Assert.assertEquals("localhost:1002", result.get(2));
    }

    @Test
    public void defineStringListProcessArgumentWithShortNameAndGetValue() {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineStringList("p", "peers", "localhost:3000,localhost:3001");

        processor.processArguments(new String[] { "-p", "localhost:1000,localhost:1001,localhost:1002" });

        List<String> result = processor.getStringList("peers");

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("localhost:1000", result.get(0));
        Assert.assertEquals("localhost:1001", result.get(1));
        Assert.assertEquals("localhost:1002", result.get(2));
    }
}

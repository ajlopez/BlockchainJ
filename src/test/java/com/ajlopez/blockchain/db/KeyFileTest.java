package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.VirtualMachineException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

/**
 * Created by ajlopez on 21/10/2019.
 */
public class KeyFileTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void writeAndReadKey() throws IOException {
        KeyFile keyFile = new KeyFile("kftest1.data", 32);

        byte[] key = FactoryHelper.createRandomBytes(32);

        keyFile.writeKey(key, 0L, 42);

        ValueInfo result = keyFile.readKey(key);

        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.position);
        Assert.assertEquals(42, result.length);
    }

    @Test
    public void cannotWriteNullKey() throws IOException {
        KeyFile keyFile = new KeyFile("kftest2.data", 32);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("invalid key");

        keyFile.writeKey(null, 0L, 42);
    }

    @Test
    public void cannotWriteKeyWithInvalidLength() throws IOException {
        KeyFile keyFile = new KeyFile("kftest3.data", 32);

        byte[] key = FactoryHelper.createRandomBytes(42);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("invalid key");

        keyFile.writeKey(key, 0L, 42);
    }
}

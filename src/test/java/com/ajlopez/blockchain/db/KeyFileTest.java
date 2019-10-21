package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 21/10/2019.
 */
public class KeyFileTest {
    @Test
    public void writeKey() throws IOException {
        KeyFile keyFile = new KeyFile("kftest1.data", 32);

        byte[] key = FactoryHelper.createRandomBytes(32);

        keyFile.writeKey(key, 0L, 42);
    }
}

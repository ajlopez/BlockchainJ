package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.store.KeyValueStore;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class KeyValueDb implements KeyValueStore {
    private final ValueFile valueFile;
    private final KeyFile keyFile;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public KeyValueDb(String name, int keyLength) throws IOException {
        this.valueFile = new ValueFile(name + ".values");
        this.keyFile = new KeyFile(name + ".keys", keyLength);
    }

    @Override
    public void setValue(byte[] key, byte[] value) throws IOException {
        this.lock.writeLock().lock();

        try {
            if (this.keyFile.containsKey(key)) {
                byte[] oldvalue = this.getValue(key);

                if (!Arrays.equals(value, oldvalue))
                    throw new IllegalStateException("cannot change value for key");

                return;
            }

            long position = this.valueFile.writeValue(value);
            this.keyFile.writeKey(key, position, value.length);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public byte[] getValue(byte[] key) throws IOException {
        this.lock.readLock().lock();

        try {
            ValueInfo valueInfo = this.keyFile.readKey(key);

            if (valueInfo == null)
                return null;

            byte[] buffer = new byte[valueInfo.length];

            // TODO Check read
            int read = this.valueFile.readValue(valueInfo.position, buffer);

            return buffer;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}

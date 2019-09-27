package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 28/10/2017.
 */
public class HashUtilsTest {
    @Test
    public void getKeccak256Hash() {
        byte[] hash = HashUtils.keccak256(new byte[] { 0x01, 0x02, 0x03 });

        // TODO better check
        Assert.assertNotNull(hash);
    }

    @Test
    public void getAddress() {
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] hash = HashUtils.keccak256(bytes);

        Address address = HashUtils.calculateAddress(bytes);

        Assert.assertNotNull(address);

        byte[] abytes = address.getBytes();

        Assert.assertNotNull(abytes);
        Assert.assertEquals(Address.ADDRESS_BYTES, abytes.length);

        byte[] ahash = new byte[Address.ADDRESS_BYTES];
        System.arraycopy(hash, Hash.HASH_BYTES - Address.ADDRESS_BYTES, ahash, 0, Address.ADDRESS_BYTES);

        Assert.assertArrayEquals(ahash, abytes);
    }
}

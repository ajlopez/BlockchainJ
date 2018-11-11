package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class AddressTest {
    @Test
    public void createAddress() {
        Random random = new Random();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        Address address = new Address(bytes);

        Assert.assertArrayEquals(bytes, address.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAddressWithInvalidBytesLength() {
        Random random = new Random();
        byte[] bytes = new byte[30];
        random.nextBytes(bytes);

        new Address(bytes);
    }

    @Test
    public void createAddressWithInitialBytes() {
        Address address = new Address();

        Assert.assertNotNull(address.getBytes());
        Assert.assertEquals(20, address.getBytes().length);
    }

    @Test
    public void createAddressWithFewBytes() {
        byte[] bytes = new byte[] { 0x01, 0x02 };
        byte[] expected = ByteUtils.copyBytes(bytes, Address.ADDRESS_LENGTH);

        Address address = new Address(bytes);

        Assert.assertNotNull(address.getBytes());
        Assert.assertEquals(Address.ADDRESS_LENGTH, address.getBytes().length);
        Assert.assertArrayEquals(expected, address.getBytes());
    }

    @Test
    public void addressWithTheSameBytesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        Address address1 = new Address(bytes);
        Address address2 = new Address(bytes);

        Assert.assertEquals(address1, address2);
        Assert.assertTrue(address1.equals(address2));
        Assert.assertTrue(address2.equals(address1));
        Assert.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void addressesWithTheSameBytesValuesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        byte[] bytes2 = Arrays.copyOf(bytes, bytes.length);

        Address address1 = new Address(bytes);
        Address address2 = new Address(bytes2);

        Assert.assertEquals(address1, address2);
        Assert.assertTrue(address1.equals(address2));
        Assert.assertTrue(address2.equals(address1));
        Assert.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void notEqual() {
        Random random = new Random();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        Address address = new Address(bytes);

        Assert.assertFalse(address.equals(null));
        Assert.assertFalse(address.equals("foo"));
    }
}

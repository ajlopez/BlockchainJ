package com.ajlopez.blockchain.core.types;

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
        byte[] bytes = new byte[Address.ADDRESS_BYTES];
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
        Assert.assertEquals(Address.ADDRESS_BYTES, address.getBytes().length);
    }

    @Test
    public void createAddressWithFewBytes() {
        byte[] bytes = new byte[] { 0x01, 0x02 };
        byte[] expected = ByteUtils.copyBytes(bytes, Address.ADDRESS_BYTES);

        Address address = new Address(bytes);

        Assert.assertNotNull(address.getBytes());
        Assert.assertEquals(Address.ADDRESS_BYTES, address.getBytes().length);
        Assert.assertArrayEquals(expected, address.getBytes());
    }

    @Test
    public void addressWithTheSameBytesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[Address.ADDRESS_BYTES];
        random.nextBytes(bytes);

        Address address1 = new Address(bytes);
        Address address2 = new Address(bytes);

        Assert.assertEquals(address1, address2);
        Assert.assertTrue(address1.equals(address2));
        Assert.assertTrue(address2.equals(address1));
        Assert.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void addressWithTheSameBytesWithDifferenPaddingAreEqual() {
        byte[] bytes1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] bytes2 = new byte[] { 0x00, 0x01, 0x02, 0x03 };

        Address address1 = new Address(bytes1);
        Address address2 = new Address(bytes2);

        Assert.assertEquals(address1, address2);
        Assert.assertTrue(address1.equals(address2));
        Assert.assertTrue(address2.equals(address1));
        Assert.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void toStringWithFewBytes() {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03 };

        Address address = new Address(bytes);

        Assert.assertEquals("0x0000000000000000000000000000000000010203", address.toString());
    }

    @Test
    public void addressesWithTheSameBytesValuesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[Address.ADDRESS_BYTES];
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
        byte[] bytes = new byte[Address.ADDRESS_BYTES];
        random.nextBytes(bytes);

        Address address = new Address(bytes);

        Assert.assertFalse(address.equals(null));
        Assert.assertFalse(address.equals("foo"));
    }
}

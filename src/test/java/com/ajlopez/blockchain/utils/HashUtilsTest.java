package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.RLP;
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

    @Test
    public void calculateNewAddressWithNonceZero() {
        byte[] bytes = FactoryHelper.createRandomBytes(20);
        byte[] nonceBytes = new byte[1];

        Address address = new Address(bytes);

        Address result = HashUtils.calculateNewAddress(address, 0);

        byte[] rlp = RLP.encodeList(RLP.encode(bytes), RLP.encode(nonceBytes));

        // TODO Check with ethereum implementation
        Assert.assertEquals(HashUtils.calculateAddress(rlp), result);
    }

    @Test
    public void calculateNewAddressWithNonceOneHundred() {
        byte[] bytes = FactoryHelper.createRandomBytes(20);
        byte[] nonceBytes = ByteUtils.unsignedLongToNormalizedBytes(100);

        Address address = new Address(bytes);

        Address result = HashUtils.calculateNewAddress(address, 100);

        byte[] rlp = RLP.encodeList(RLP.encode(bytes), RLP.encode(nonceBytes));

        // TODO Check with ethereum implementation
        Assert.assertEquals(HashUtils.calculateAddress(rlp), result);
    }
}

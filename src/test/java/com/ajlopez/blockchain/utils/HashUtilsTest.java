package com.ajlopez.blockchain.utils;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by ajlopez on 28/10/2017.
 */
public class HashUtilsTest {
    @Test
    public void getKeccak256Hash() throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] hash = HashUtils.keccak256(new byte[] { 0x01, 0x02, 0x03 });

        // TODO better check
        Assert.assertNotNull(hash);
    }
}

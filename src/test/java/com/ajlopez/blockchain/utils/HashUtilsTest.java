package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.PeerId;
import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

/**
 * Created by ajlopez on 28/10/2017.
 */
public class HashUtilsTest {
    @Test
    public void getSha3Hash() throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] hash = HashUtils.sha3(new byte[] { 0x01, 0x02, 0x03 });

        Assert.assertNotNull(hash);
    }

    public static Hash generateRandomHash() {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        return new Hash(bytes);
    }

    public static PeerId generateRandomPeerId() {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        return new PeerId(bytes);
    }
}

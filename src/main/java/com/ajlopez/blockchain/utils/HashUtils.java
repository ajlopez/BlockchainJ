package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.crypto.SpongyCastleProvider;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

/**
 * Created by ajlopez on 28/10/2017.
 */
public class HashUtils {
    static {
        Security.addProvider(SpongyCastleProvider.getInstance());
    }

    public static byte[] sha3(byte[] input) throws NoSuchProviderException, NoSuchAlgorithmException {
        MessageDigest digest;
        digest = MessageDigest.getInstance("KECCAK-256", BouncyCastleProvider.PROVIDER_NAME);
        digest.update(input);
        return digest.digest();
    }
}

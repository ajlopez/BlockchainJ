package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.core.Hash;
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
    public static final int HASH_BYTES = 32;

    static {
        Security.addProvider(SpongyCastleProvider.getInstance());
    }

    private HashUtils() { }

    public static byte[] sha3(byte[] input) throws NoSuchProviderException, NoSuchAlgorithmException {
        MessageDigest digest;
        digest = MessageDigest.getInstance("KECCAK-256", BouncyCastleProvider.PROVIDER_NAME);
        digest.update(input);
        return digest.digest();
    }

    public static Hash calculateHash(byte[] data) {
        try {
            return new Hash(sha3(data));
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}

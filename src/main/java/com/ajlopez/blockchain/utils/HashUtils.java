package com.ajlopez.blockchain.utils;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.crypto.SpongyCastleProvider;
import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.encoding.RLPEncoder;
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

    public static byte[] keccak256(byte[] input) {
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance("KECCAK-256", BouncyCastleProvider.PROVIDER_NAME);
            digest.update(input);
            return digest.digest();
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Hash calculateHash(byte[] data) {
        return new Hash(keccak256(data));
    }

    public static Address calculateAddress(byte[] data) {
        byte[] bytes = keccak256(data);
        byte[] abytes = new byte[Address.ADDRESS_BYTES];
        System.arraycopy(bytes, Hash.HASH_BYTES - Address.ADDRESS_BYTES, abytes, 0, Address.ADDRESS_BYTES);
        return new Address(abytes);
    }

    public static Address calculateNewAddress(Address address, long nonce) {
        byte[] rlpAddress = RLPEncoder.encodeAddress(address);
        byte[] rlpNonce = RLPEncoder.encodeUnsignedLong(nonce);

        return calculateAddress(RLP.encodeList(rlpAddress, rlpNonce));
    }
}

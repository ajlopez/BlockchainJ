package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoder {
    private AccountEncoder() { }

    public static byte[] encode(Account account) {
        if (account.isEmpty())
            throw new IllegalArgumentException("Account is empty");

        // TODO improve encoding empty or semi-empty account
        byte[] rlpBalance = RLPEncoder.encodeCoin(account.getBalance());
        byte[] rlpNonce = RLPEncoder.encodeUnsignedLong(account.getNonce());

        if (account.getCodeLength() == 0 && account.getCodeHash() == null && account.getStorageHash() == null)
            if (account.getNonce() == 0)
                return RLP.encodeList(rlpBalance);
            else
                return RLP.encodeList(rlpBalance, rlpNonce);

        byte[] rlpCodeLength = RLPEncoder.encodeUnsignedLong(account.getCodeLength());
        byte[] rlpCodeHash = RLPEncoder.encodeHash(account.getCodeHash());

        byte[] rlpStorageHash = RLPEncoder.encodeHash(account.getStorageHash());

        return RLP.encodeList(rlpBalance, rlpNonce, rlpCodeLength, rlpCodeHash, rlpStorageHash);
    }

    public static Account decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        // TODO check other possible counts
        if (bytes.length > 5)
            throw new IllegalArgumentException("Invalid account encoding");

        Coin balance = RLPEncoder.decodeCoin(bytes[0]);

        if (bytes.length == 1)
            return new Account(balance, 0, 0, null, null);

        long nonce = RLPEncoder.decodeUnsignedLong(bytes[1]);

        if (bytes.length == 2)
            return new Account(balance, nonce, 0, null, null);

        long codeLength = RLPEncoder.decodeUnsignedLong(bytes[2]);
        Hash codeHash = RLPEncoder.decodeHash(bytes[3]);
        Hash storageHash = RLPEncoder.decodeHash(bytes[4]);

        return new Account(balance, nonce, codeLength, codeHash, storageHash);
    }
}

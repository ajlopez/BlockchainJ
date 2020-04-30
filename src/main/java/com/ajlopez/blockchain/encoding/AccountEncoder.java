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
        byte[] rlpBalance = RLPEncoder.encodeCoin(account.getBalance());
        byte[] rlpNonce = RLPEncoder.encodeUnsignedLong(account.getNonce());

        if (account.getCodeHash() == null && account.getStorageHash() == null)
            return RLP.encodeList(rlpBalance, rlpNonce);

        byte[] rlpCodeHash = RLPEncoder.encodeHash(account.getCodeHash());
        byte[] rlpStorageHash = RLPEncoder.encodeHash(account.getStorageHash());

        return RLP.encodeList(rlpBalance, rlpNonce, rlpCodeHash, rlpStorageHash);
    }

    public static Account decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        Coin balance = RLPEncoder.decodeCoin(bytes[0]);
        long nonce = RLPEncoder.decodeUnsignedLong(bytes[1]);

        if (bytes.length == 2)
            return new Account(balance, nonce, null, null);

        Hash codeHash = RLPEncoder.decodeHash(bytes[2]);
        Hash storageHash = RLPEncoder.decodeHash(bytes[3]);

        return new Account(balance, nonce, codeHash, storageHash);
    }
}

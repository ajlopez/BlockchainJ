package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoder {
    private AccountEncoder() { }

    public static byte[] encode(Account account) {
        byte[] rlpBalance = RLPEncoder.encodeCoin(account.getBalance());
        byte[] rlpNonce = RLPEncoder.encodeUnsignedLong(account.getNonce());
        byte[] rlpCodeHash = RLPEncoder.encodeHash(account.getCodeHash());

        return RLP.encodeList(rlpBalance, rlpNonce, rlpCodeHash);
    }

    public static Account decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        BigInteger balance = RLPEncoder.decodeCoin(bytes[0]);
        long nonce = RLPEncoder.decodeUnsignedLong(bytes[1]);
        Hash codeHash = RLPEncoder.decodeHash(bytes[2]);

        return new Account(balance, nonce, codeHash, null);
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoder {
    private AccountEncoder() { }

    public static byte[] encode(Account account) {
        byte[] rlpBalance = RLP.encode(account.getBalance().toByteArray());
        byte[] rlpNonce = RLP.encode(ByteUtils.unsignedLongToNormalizedBytes(account.getNonce()));
        Hash codeHash = account.getCodeHash();
        byte[] rlpCodeHash = codeHash == null ? RLP.encode(ByteUtils.EMPTY_BYTE_ARRAY) : RLP.encode(((Hash) codeHash).getBytes());

        return RLP.encodeList(rlpBalance, rlpNonce, rlpCodeHash);
    }

    public static Account decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        BigInteger balance = new BigInteger(1, RLP.decode(bytes[0]));
        byte[] nonce = RLP.decode(bytes[1]);
        byte[] codeHashBytes = RLP.decode(bytes[2]);
        Hash codeHash = codeHashBytes.length == 0 ? null : new Hash(codeHashBytes);

        return new Account(balance, ByteUtils.bytesToUnsignedLong(nonce), codeHash);
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoder {
    private AccountEncoder() { }

    public static byte[] encode(Account state) {
        byte[] rlpBalance = RLP.encode(state.getBalance().toByteArray());
        byte[] rlpNonce = RLP.encode(ByteUtils.unsignedLongToBytes(state.getNonce()));

        return RLP.encodeList(rlpBalance, rlpNonce);
    }

    public static Account decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        BigInteger balance = new BigInteger(1, RLP.decode(bytes[0]));
        byte[] nonce = RLP.decode(bytes[1]);

        return new Account(balance, ByteUtils.bytesToUnsignedLong(nonce));
    }
}

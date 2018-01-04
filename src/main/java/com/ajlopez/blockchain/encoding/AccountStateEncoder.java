package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.AccountState;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountStateEncoder {
    private AccountStateEncoder() { }

    public static byte[] encode(AccountState state) {
        byte[] rlpBalance = RLP.encode(state.getBalance().toByteArray());
        byte[] rlpNonce = RLP.encode(ByteUtils.unsignedLongToBytes(state.getNonce()));

        return RLP.encodeList(rlpBalance, rlpNonce);
    }

    public static AccountState decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        BigInteger balance = new BigInteger(1, RLP.decode(bytes[0]));
        byte[] nonce = RLP.decode(bytes[1]);

        return new AccountState(balance, ByteUtils.bytesToUnsignedLong(nonce));
    }
}

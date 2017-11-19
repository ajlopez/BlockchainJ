package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.AccountState;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountStateEncoder {
    private AccountStateEncoder() { }

    public static byte[] encode(AccountState state) {
        byte[] rlpBalance = RLP.encode(state.getBalance().toByteArray());

        return RLP.encodeList(rlpBalance);
    }

    public static AccountState decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        BigInteger balance = new BigInteger(1, RLP.decode(bytes[0]));

        AccountState state = new AccountState();

        state.addToBalance(balance);

        return state;
    }
}

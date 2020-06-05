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

        if (account.getCodeLength() == 0 && account.getCodeHash() == null && account.getStorageHash() == null)
            return RLP.encodeList(rlpBalance, rlpNonce);

        byte[] rlpCodeLength = RLPEncoder.encodeUnsignedLong(account.getCodeLength());
        byte[] rlpCodeHash = RLPEncoder.encodeHash(account.getCodeHash());
        byte[] rlpStorageHash = RLPEncoder.encodeHash(account.getStorageHash());

        return RLP.encodeList(rlpBalance, rlpNonce, rlpCodeLength, rlpCodeHash, rlpStorageHash);
    }

    public static Account decode(byte[] encoded) {
        // TODO check the number of parts
        byte[][] bytes = RLP.decodeList(encoded);

        Coin balance = RLPEncoder.decodeCoin(bytes[0]);
        long nonce = RLPEncoder.decodeUnsignedLong(bytes[1]);

        if (bytes.length == 2)
            return new Account(balance, nonce, 0, null, null);

        long codeLength = RLPEncoder.decodeUnsignedLong(bytes[2]);
        Hash codeHash = RLPEncoder.decodeHash(bytes[3]);
        Hash storageHash = RLPEncoder.decodeHash(bytes[4]);

        return new Account(balance, nonce, codeLength, codeHash, storageHash);
    }
}

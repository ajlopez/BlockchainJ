package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Address;
import com.ajlopez.blockchain.core.Transaction;

import java.math.BigInteger;

/**
 * Created by ajlopez on 04/10/2017.
 */
public class TransactionEncoder {
    public byte[] encode(Transaction transaction) {
        byte[] rlpSender = RLP.encode(transaction.getSender().getBytes());
        byte[] rlpReceiver = RLP.encode(transaction.getReceiver().getBytes());
        byte[] rlpValue = RLP.encode(transaction.getValue().toByteArray());

        return RLP.encodeList(rlpSender, rlpReceiver, rlpValue);
    }

    public Transaction decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        Address sender = new Address(RLP.decode(bytes[0]));
        Address receiver = new Address(RLP.decode(bytes[1]));
        BigInteger value = new BigInteger(1, RLP.decode(bytes[2]));

        return new Transaction(sender, receiver, value);
    }
}

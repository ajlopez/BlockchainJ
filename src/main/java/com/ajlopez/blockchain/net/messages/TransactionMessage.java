package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.encoding.TransactionEncoder;

/**
 * Created by ajlopez on 20/01/2018.
 */
public class TransactionMessage extends Message {
    private Transaction transaction;

    public TransactionMessage(Transaction transaction) {
        super(MessageType.TRANSACTION);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    @Override
    public byte[] getPayload() {
        return TransactionEncoder.encode(this.transaction);
    }
}

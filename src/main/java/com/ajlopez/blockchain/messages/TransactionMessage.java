package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.encoding.TransactionEncoder;

/**
 * Created by ajlopez on 20/01/2018.
 */
public class TransactionMessage extends Message {
    public TransactionMessage(Transaction tx) {
        super(MessageType.TRANSACTION, TransactionEncoder.encode(tx));
    }

    public Transaction getTransaction() {
        return TransactionEncoder.decode(this.getPayload());
    }
}

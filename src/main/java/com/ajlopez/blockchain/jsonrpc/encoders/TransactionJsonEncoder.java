package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonObjectBuilder;
import com.ajlopez.blockchain.json.JsonValue;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class TransactionJsonEncoder {
    private TransactionJsonEncoder() {}

    public static JsonValue encode(Transaction transaction) {
        return (new JsonObjectBuilder(new JsonBuilder()))
                .name("hash")
                .value(transaction.getHash())
                .name("from")
                .value(transaction.getSender())
                .name("to")
                .value(transaction.getReceiver())
                .name("nonce")
                .value(transaction.getNonce())
                .name("value")
                .value(transaction.getValue())
                .name("gas")
                .value(transaction.getGas())
                .name("gasPrice")
                .value(transaction.getGasPrice())
                .build();
    }
}

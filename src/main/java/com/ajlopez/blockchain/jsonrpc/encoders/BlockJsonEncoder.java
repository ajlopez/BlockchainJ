package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonObjectBuilder;
import com.ajlopez.blockchain.json.JsonValue;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class BlockJsonEncoder {
    private BlockJsonEncoder() {}

    public static JsonValue encode(Block block) {
        return (new JsonObjectBuilder(new JsonBuilder()))
                .name("hash")
                .value(block.getHash())
                .name("number")
                .value(block.getNumber())
                .name("parentHash")
                .value(block.getParentHash())
                .name("nonce")
                .value(0)
                .name("miner")
                .value(block.getCoinbase())
                .name("transactionRoot")
                .value(block.getTransactionRootHash())
                .name("stateRoot")
                .value(block.getStateRootHash())
                .name("timestamp")
                .value(block.getTimestamp())
                .name("uncles")
                .array()
                .end()
                .name("transactions")
                .array()
                .end()
                .build();
    }
}

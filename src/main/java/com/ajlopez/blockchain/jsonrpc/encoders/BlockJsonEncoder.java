package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonNullValue;
import com.ajlopez.blockchain.json.JsonObjectBuilder;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.utils.HexUtils;

import java.util.List;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class BlockJsonEncoder {
    private BlockJsonEncoder() {}

    public static JsonValue encode(Block block, Difficulty totalDifficulty, boolean fullTransactions) {
        if (block == null)
            return JsonNullValue.getInstance();

        JsonBuilder builder = (new JsonObjectBuilder(new JsonBuilder()))
                .name("hash")
                .value(block.getHash())
                .name("number")
                .value(HexUtils.unsignedLongToHexValue(block.getNumber()))
                .name("parentHash")
                .value(block.getParentHash())
                .name("nonce")
                .value("0x0")
                .name("miner")
                .value(block.getCoinbase())
                .name("transactionRoot")
                .value(block.getTransactionsRootHash())
                .name("stateRoot")
                .value(block.getStateRootHash())
                .name("timestamp")
                .value(HexUtils.unsignedLongToHexValue(block.getTimestamp()))
                .name("gasLimit")
                .value(HexUtils.unsignedLongToHexValue(block.getGasLimit()))
                .name("difficulty")
                .value(block.getDifficulty())
                .name("totalDifficulty")
                .value(totalDifficulty);

        List<Transaction> transactions = block.getTransactions();

        builder = builder.name("transactions").array();

        for (Transaction tx: transactions)
            if (fullTransactions)
                builder = builder.value(TransactionJsonEncoder.encode(tx, true, true));
            else
                builder = builder.value(tx.getHash());

        builder = builder.end();

        List<BlockHeader> uncles = block.getUncles();

        builder = builder.name("uncles").array();

        for (BlockHeader uncle: uncles)
            builder = builder.value(uncle.getHash());

        builder = builder.end();

        return builder.build();
    }
}

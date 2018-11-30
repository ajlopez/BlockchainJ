package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by Angel on 11/30/2018.
 */
public class BlocksProcessor implements JsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    public JsonRpcResponse processRequest(JsonRpcRequest request) {
        return new JsonRpcResponse(request.getId(), request.getVersion(), JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedLongToNormalizedBytes(this.blockChain.getBestBlockNumber()), true)));
    }
}

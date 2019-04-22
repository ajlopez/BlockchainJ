package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.jsonrpc.encoders.BlockJsonEncoder;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessor extends AbstractJsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        if (request.check("eth_blockNumber", 0))
            return this.getBestBlockNumber(request);

        // TODO use the second argument
        if (request.check("eth_getBlockByNumber", 1, 2))
            return this.getBlockByNumber(request);

        if (request.check("eth_getBlockByHash", 1))
            return this.getBlockByHash(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getBestBlockNumber(JsonRpcRequest request) {
        return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockNumber());
    }

    private JsonRpcResponse getBlockByNumber(JsonRpcRequest request) {
        String blockId = request.getParams().get(0).getValue().toString();
        Block block;

        if ("earliest".equals(blockId))
            block = this.blockChain.getBlockByNumber(0);
        else if ("latest".equals(blockId))
            block = this.blockChain.getBestBlock();
        else if (blockId.startsWith("0x"))
            block = this.blockChain.getBlockByNumber(Long.parseLong(blockId.substring(2), 16));
        else
            block = this.blockChain.getBlockByNumber(Long.parseLong(blockId));

        JsonValue json = BlockJsonEncoder.encode(block);

        return JsonRpcResponse.createResponse(request, json);
    }

    private JsonRpcResponse getBlockByHash(JsonRpcRequest request) {
        BlockHash hash = new BlockHash(HexUtils.hexStringToBytes(request.getParams().get(0).getValue().toString()));
        Block block = this.blockChain.getBlockByHash(hash);

        JsonValue json = BlockJsonEncoder.encode(block);

        return JsonRpcResponse.createResponse(request, json);
    }
}

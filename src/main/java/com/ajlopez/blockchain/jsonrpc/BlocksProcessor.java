package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessor implements JsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        String method = request.getMethod();

        if (request.check("eth_blockNumber", 0))
            return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockNumber());

        throw new JsonRpcException(String.format("Unknown method '%s'", method));
    }
}

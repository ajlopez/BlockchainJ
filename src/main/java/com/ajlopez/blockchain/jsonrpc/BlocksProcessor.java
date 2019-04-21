package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.jsonrpc.encoders.BlockJsonEncoder;

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
            return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockNumber());

        if (request.check("eth_getBlockByNumber", 1))
            return JsonRpcResponse.createResponse(request, BlockJsonEncoder.encode(this.blockChain.getBlockByNumber(Long.parseLong(request.getParams().get(0).getValue().toString()))));

        return super.processRequest(request);
    }
}

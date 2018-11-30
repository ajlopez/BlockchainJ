package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessor implements JsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    public JsonRpcResponse processRequest(JsonRpcRequest request) {
        String method = request.getMethod();

        if (!"eth_blockNumber".equals(method))
            throw new UnsupportedOperationException(String.format("Unknown method '%s'", method));

        int nparams = request.getParams().size();

        if (nparams != 0)
            throw new UnsupportedOperationException(String.format("Invalid number of parameters: expected %d found %d", 0, nparams));

        return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockNumber());
    }
}

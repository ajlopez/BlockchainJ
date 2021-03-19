package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.BlockInformation;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.json.JsonBooleanValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.jsonrpc.encoders.BlockJsonEncoder;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessor extends AbstractJsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        if (request.check("eth_blockNumber", 0))
            return this.getBestBlockNumber(request);

        // TODO use the second argument
        if (request.check("eth_getBlockByNumber", 1, 2))
            return this.getBlockByNumber(request);

        if (request.check("eth_getBlockByHash", 1, 2))
            return this.getBlockByHash(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getBestBlockNumber(JsonRpcRequest request) throws IOException {
        return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockInformation().getBlockNumber());
    }

    private JsonRpcResponse getBlockByNumber(JsonRpcRequest request) throws IOException {
        String blockId = request.getParams().get(0).getValue().toString();
        Block block;

        if ("earliest".equals(blockId))
            block = this.blockChain.getBlockByNumber(0);
        else if ("latest".equals(blockId))
            block = this.blockChain.getBestBlockInformation().getBlock();
        else if (blockId.startsWith("0x"))
            block = this.blockChain.getBlockByNumber(Long.parseLong(blockId.substring(2), 16));
        else
            block = this.blockChain.getBlockByNumber(Long.parseLong(blockId));

        JsonValue json;

        if (block != null) {
            BlockInformation blockInformation = this.blockChain.getBlockInformation(block.getNumber(), block.getHash());
            json = BlockJsonEncoder.encode(block, blockInformation.getTotalDifficulty(), false);
        }
        else
            json = BlockJsonEncoder.encode(null, null, false);

        return JsonRpcResponse.createResponse(request, json);
    }

    private JsonRpcResponse getBlockByHash(JsonRpcRequest request) throws IOException {
        BlockHash hash = new BlockHash(HexUtils.hexStringToBytes(request.getParams().get(0).getValue().toString()));
        boolean fullTransactions = false;

        if (request.getParams().size() > 1)
            fullTransactions = (Boolean)(request.getParams().get(1)).getValue();

        Block block = this.blockChain.getBlockByHash(hash);
        JsonValue json;

        if (block != null) {
            BlockInformation blockInformation = this.blockChain.getBlockInformation(block.getNumber(), block.getHash());

            json = BlockJsonEncoder.encode(block, blockInformation.getTotalDifficulty(), fullTransactions);
        }
        else
            json = BlockJsonEncoder.encode(null, null, false);

        return JsonRpcResponse.createResponse(request, json);
    }
}

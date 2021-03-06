package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;

import java.io.IOException;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class BlocksProvider {
    private final BlockChain blockChain;

    public BlocksProvider(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    public Block getBlock(String blockId) throws JsonRpcException, IOException {
        if ("latest".equals(blockId))
            return blockChain.getBestBlockInformation().getBlock();
        if ("earliest".equals(blockId))
            return blockChain.getBlockByNumber(0);
        if ("pending".equals(blockId))
            throw new JsonRpcException("Unsupported block id 'pending'");

        return this.blockChain.getBlockByNumber(toLongNumber(blockId));
    }

    private static long toLongNumber(String text) throws JsonRpcException {
        try {
            return Long.decode(text);
        }
        catch (NumberFormatException ex) {
            throw new JsonRpcException("Invalid number format");
        }
    }
}

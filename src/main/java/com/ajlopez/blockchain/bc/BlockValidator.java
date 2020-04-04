package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.state.Trie;

import java.io.IOException;

/**
 * Created by ajlopez on 01/06/2019.
 */
public class BlockValidator {
    private final BlockExecutor blockExecutor;

    public BlockValidator(BlockExecutor blockExecutor) {
        this.blockExecutor = blockExecutor;
    }

    public boolean isValid(Block block) {
        if (!Block.calculateTransactionsRootHash(block.getTransactions()).equals(block.getTransactionRootHash()))
            return false;

        return true;
    }

    public boolean isValid(Block block, Block parent) throws IOException {
        if (parent == null)
            return true;

        Hash initialStateRoot = parent.getStateRootHash();

        Hash hash = this.blockExecutor.executeBlock(block, initialStateRoot);

        return hash.equals(block.getStateRootHash());
    }
}

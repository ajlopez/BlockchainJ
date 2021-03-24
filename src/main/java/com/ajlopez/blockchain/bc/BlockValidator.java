package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.BlockExecutionResult;
import com.ajlopez.blockchain.execution.BlockExecutor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajlopez on 01/06/2019.
 */
public class BlockValidator {
    // TODO review the number
    private static final int MAX_NO_UNCLES = 2;

    private final BlockExecutor blockExecutor;

    public BlockValidator(BlockExecutor blockExecutor) {
        this.blockExecutor = blockExecutor;
    }

    public boolean isValid(Block block) {
        if (block.getTransactions().size() != block.getHeader().getTransactionsCount())
            return false;

        if (!Block.calculateTransactionsRootHash(block.getTransactions()).equals(block.getTransactionsRootHash()))
            return false;

        if (!Block.calculateUnclesRootHash(block.getUncles()).equals(block.getUnclesRootHash()))
            return false;

        return unclesAreValid(block);
    }

    private static boolean unclesAreValid(Block block) {
        if (block.getUnclesCount() > MAX_NO_UNCLES)
            return false;

        Set<Hash> uncleHashes = new HashSet<>();

        for (BlockHeader uncle : block.getUncles()) {
            if (uncle.getNumber() >= block.getNumber())
                return false;

            Hash uncleHash = uncle.getHash();

            if (uncleHashes.contains(uncleHash))
                return false;

            uncleHashes.add(uncleHash);
        }

        return true;
    }

    public boolean isValid(Block block, Block parent) throws IOException {
        if (parent == null)
            return true;

        Hash initialStateRoot = parent.getStateRootHash();

        BlockExecutionResult blockExecutionResult = this.blockExecutor.executeBlock(block, initialStateRoot);

        if (!blockExecutionResult.getStateRootHash().equals(block.getStateRootHash()))
            return false;

        return blockExecutionResult.getTransactionReceiptsHash().equals(block.getReceiptsRootHash());
    }
}

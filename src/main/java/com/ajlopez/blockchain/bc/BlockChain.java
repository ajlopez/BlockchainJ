package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 15/08/2017.
 */
public class BlockChain implements BlockProvider {
    public static final long NO_BEST_BLOCK_NUMBER = -1;

    private Block best;
    private final BlockHashStore blocksByHash = new BlockHashStore();
    private final BlockNumberStore blocksByNumber = new BlockNumberStore();

    private List<Consumer<Block>> blockConsumers = new ArrayList<>();

    public Block getBestBlock() {
        return this.best;
    }

    public long getBestBlockNumber() {
        if (this.best == null)
            return NO_BEST_BLOCK_NUMBER;

        return this.best.getNumber();
    }

    public boolean connectBlock(Block block) {
        if (isOrphan(block))
            return false;

        if (this.blocksByHash.containsBlock(block.getHash()))
            return this.blocksByNumber.containsBlock(block);

        this.saveBlock(block);

        if (this.best == null || block.getNumber() > this.best.getNumber())
            this.saveBestBlock(block);

        this.emitBlock(block);

        return true;
    }

    private void emitBlock(Block block) {
        for (Consumer<Block> consumer: this.blockConsumers)
            consumer.accept(block);
    }

    public void onBlock(Consumer<Block> consumer) {
        this.blockConsumers.add(consumer);
    }

    @Override
    public Block getBlockByHash(Hash hash) {
        return this.blocksByHash.getBlock(hash);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blocksByHash.containsBlock(hash);
    }

    @Override
    public Block getBlockByNumber(long number) {
        return this.blocksByNumber.getBlock(number);
    }

    private boolean isOrphan(Block block) {
        if (block.getNumber() == 0)
            return false;

        return !blocksByHash.containsBlock(block.getParentHash());
    }

    private void saveBlock(Block block) {
        if (!this.blocksByHash.containsBlock(block.getHash()))
            this.blocksByHash.saveBlock(block);
    }

    private void saveBestBlock(Block block) {
        this.best = block;

        this.blocksByNumber.saveBlock(block);

        while (block.getNumber() > 0 && !this.blocksByNumber.getBlock(block.getNumber() - 1).getHash().equals(block.getParentHash())) {
            block = this.blocksByHash.getBlock(block.getParentHash());
            this.blocksByNumber.saveBlock(block);
        }
    }
}

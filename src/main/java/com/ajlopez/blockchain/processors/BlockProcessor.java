package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 17/12/2017.
 */
public class BlockProcessor {
    private OrphanBlocks orphanBlocks;
    private BlockChain blockChain;
    private List<Consumer<Block>> newBestBlockConsumers = new ArrayList<>();

    public BlockProcessor(BlockChain blockChain, OrphanBlocks orphanBlocks) {
        this.blockChain = blockChain;
        this.orphanBlocks = orphanBlocks;
    }

    public void processBlock(Block block) {
        Hash hash = block.getHash();

        if (this.orphanBlocks.isKnownOrphan(hash))
            return;

        if (this.blockChain.isChainedBlock(hash))
            return;

        Block initialBestBlock = this.getBestBlock();

        if (blockChain.connectBlock(block)) {
            connectDescendants(block);
            Block newBestBlock = this.getBestBlock();

            if (initialBestBlock == null || !newBestBlock.getHash().equals(initialBestBlock.getHash()))
                emitNewBestBlock(newBestBlock);
        }
        else
            orphanBlocks.addToOrphans(block);
    }

    public Block getBestBlock() {
        return this.blockChain.getBestBlock();
    }

    public long getBestBlockNumber() {
        return this.blockChain.getBestBlockNumber();
    }

    public Block getBlockByHash(Hash hash) {
        return this.blockChain.getBlockByHash(hash);
    }

    public Block getBlockByNumber(long number) {
        return this.blockChain.getBlockByNumber(number);
    }

    public boolean isChainedBlock(Hash hash) {
        return this.blockChain.isChainedBlock(hash);
    }

    public boolean isOrphanBlock(Hash hash) {
        return this.orphanBlocks.isKnownOrphan(hash);
    }

    public boolean isKnownBlock(Hash hash) {
        return this.isChainedBlock(hash) || this.isOrphanBlock(hash);
    }

    public void onNewBestBlock(Consumer<Block> consumer) {
        this.newBestBlockConsumers.add(consumer);
    }

    private void connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(orphanBlocks.getChildrenOrphanBlocks(block));

        children.forEach(child -> {
            orphanBlocks.removeOrphan(child);
            processBlock(child);
        });
    }

    private void emitNewBestBlock(Block block) {
        this.newBestBlockConsumers.forEach(a -> a.accept(block));
    }
}

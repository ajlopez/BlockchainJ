package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.types.BlockHash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 17/12/2017.
 */
public class BlockProcessor {
    private static List<Block> emptyList = Collections.unmodifiableList(Arrays.asList());

    private OrphanBlocks orphanBlocks;
    private BlockChain blockChain;
    private List<Consumer<Block>> newBestBlockConsumers = new ArrayList<>();

    public BlockProcessor(BlockChain blockChain, OrphanBlocks orphanBlocks) {
        this.blockChain = blockChain;
        this.orphanBlocks = orphanBlocks;
    }

    public List<Block> processBlock(Block block) {
        BlockHash hash = block.getHash();

        if (this.orphanBlocks.isKnownOrphan(hash))
            return emptyList;

        if (this.blockChain.isChainedBlock(hash))
            return emptyList;

        Block initialBestBlock = this.getBestBlock();

        if (blockChain.connectBlock(block)) {
            List<Block> connectedBlocks = new ArrayList<>();
            connectedBlocks.add(block);
            connectedBlocks.addAll(connectDescendants(block));

            Block newBestBlock = this.getBestBlock();

            if (initialBestBlock == null || !newBestBlock.getHash().equals(initialBestBlock.getHash()))
                emitNewBestBlock(newBestBlock);

            return connectedBlocks;
        }
        else {
            orphanBlocks.addToOrphans(block);
            return emptyList;
        }
    }

    public Block getBestBlock() {
        return this.blockChain.getBestBlock();
    }

    public long getBestBlockNumber() {
        return this.blockChain.getBestBlockNumber();
    }

    public Block getBlockByHash(BlockHash hash) {
        return this.blockChain.getBlockByHash(hash);
    }

    public Block getBlockByNumber(long number) {
        return this.blockChain.getBlockByNumber(number);
    }

    public boolean isChainedBlock(BlockHash hash) {
        return this.blockChain.isChainedBlock(hash);
    }

    public boolean isOrphanBlock(BlockHash hash) {
        return this.orphanBlocks.isKnownOrphan(hash);
    }

    public boolean isKnownBlock(BlockHash hash) {
        return this.isChainedBlock(hash) || this.isOrphanBlock(hash);
    }

    public BlockHash getUnknownAncestorHash(BlockHash hash) {
        return this.orphanBlocks.getUnknownAncestorHash(hash);
    }

    public void onNewBestBlock(Consumer<Block> consumer) {
        this.newBestBlockConsumers.add(consumer);
    }

    private List<Block> connectDescendants(Block block) {
        List<Block> children = new ArrayList<>(orphanBlocks.getChildrenOrphanBlocks(block));
        List<Block> connected = new ArrayList<>();

        children.forEach(child -> {
            orphanBlocks.removeOrphan(child);
            connected.addAll(processBlock(child));
        });

        return connected;
    }

    private void emitNewBestBlock(Block block) {
        this.newBestBlockConsumers.forEach(a -> a.accept(block));
    }
}

package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.BlockHash;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ajlopez on 19/01/2021.
 */
public class BlockUtils {
    private BlockUtils() {

    }

    public static Set<BlockHeader> getAncestorsHeaders(Block block, int depth, BlockStore blockStore) throws IOException {
        return getAncestorsHeaders(block.getParentHash(), depth, blockStore);
    }

    public static Set<BlockHeader> getAncestorsHeaders(BlockHash parentHash, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> ancestors = new HashSet<>();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);
            ancestors.add(parent.getHeader());

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return ancestors;
    }

    public static Set<BlockHeader> getAncestorsAllHeaders(Block block, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> headers = new HashSet<>();
        BlockHash parentHash = block.getParentHash();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);
            headers.add(parent.getHeader());
            headers.addAll(parent.getUncles());

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return headers;
    }

    public static Set<BlockHeader> getPreviousAllHeaders(Block block, int depth, BlockStore blockStore, BlocksInformationStore blocksInformationStore) throws IOException {
        return getPreviousAllHeaders(block.getNumber(), depth, blockStore, blocksInformationStore);
    }

    public static Set<BlockHeader> getPreviousAllHeaders(long height, int depth, BlockStore blockStore, BlocksInformationStore blocksInformationStore) throws IOException {
        Set<BlockHeader> headers = new HashSet<>();

        for (int k = 0; k < depth; k++) {
            BlocksInformation blocksInformation = blocksInformationStore.get(height - k - 1);

            for (BlockInformation bi : blocksInformation.getBlockInformationList()) {
                Block b = blockStore.getBlock(bi.getBlockHash());

                headers.add(b.getHeader());
                headers.addAll(b.getUncles());
            }
        }

        return headers;
    }

    public static Set<BlockHeader> getCandidateUncles(Block block, int depth, BlockStore blockStore, BlocksInformationStore blocksInformationStore) throws IOException {
        Set<BlockHeader> candidateUncles = getPreviousAllHeaders(block, depth, blockStore, blocksInformationStore);
        Set<BlockHeader> ancestorsAllHeaders = getAncestorsAllHeaders(block, depth + 1, blockStore);
        Set<BlockHeader> ancestorsHeaders = getAncestorsHeaders(block, depth + 1, blockStore);

        candidateUncles.removeAll(ancestorsAllHeaders);

        Set<BlockHash> ancestorsHashes = ancestorsHeaders
                .stream()
                .map(bh -> bh.getHash())
                .collect(Collectors.toSet());

        Set<BlockHeader> uncles = candidateUncles
                .stream()
                .filter(u -> ancestorsHashes.contains(u.getParentHash()))
                .collect(Collectors.toSet());

        return uncles;
    }

    public static Set<Block> getAncestorsBlocks(Block block, int depth, BlockStore blockStore) throws IOException {
        Set<Block> ancestors = new HashSet<>();
        BlockHash parentHash = block.getParentHash();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);
            ancestors.add(parent);

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return ancestors;
    }

    public static Set<BlockHeader> getAncestorsUncles(Block block, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> uncles = new HashSet<>();

        Set<Block> ancestors = getAncestorsBlocks(block, depth, blockStore);

        for (Block ancestor : ancestors)
            uncles.addAll(ancestor.getUncles());

        return uncles;
    }
}

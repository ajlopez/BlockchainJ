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

    public static Set<BlockHeader> getAncestorsHeaders(BlockHash parentHash, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> ancestors = new HashSet<>();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);

            if (parent == null)
                return ancestors;

            ancestors.add(parent.getHeader());

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return ancestors;
    }

    public static Set<BlockHeader> getAncestorsAllHeaders(BlockHash parentHash, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> headers = new HashSet<>();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);

            if (parent == null)
                return headers;

            headers.add(parent.getHeader());
            headers.addAll(parent.getUncles());

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return headers;
    }

    public static Set<BlockHeader> getPreviousAllHeaders(long height, int depth, BlockStore blockStore, BlocksInformationStore blocksInformationStore) throws IOException {
        Set<BlockHeader> headers = new HashSet<>();

        for (int k = 0; k < depth && height - k - 1 >= 0; k++) {
            BlocksInformation blocksInformation = blocksInformationStore.get(height - k - 1);

            if (blocksInformation == null)
                return headers;

            for (BlockInformation bi : blocksInformation.getBlockInformationList()) {
                Block b = blockStore.getBlock(bi.getBlockHash());

                headers.add(b.getHeader());
                headers.addAll(b.getUncles());
            }
        }

        return headers;
    }

    public static Set<BlockHeader> getCandidateUncles(BlockHash parentHash, int depth, BlockStore blockStore, BlocksInformationStore blocksInformationStore) throws IOException {
        Block parentBlock = blockStore.getBlock(parentHash);

        if (parentBlock == null)
            return new HashSet();

        long height = parentBlock.getNumber() + 1;
        Set<BlockHeader> candidateUncles = getPreviousAllHeaders(height, depth, blockStore, blocksInformationStore);
        Set<BlockHeader> ancestorsAllHeaders = getAncestorsAllHeaders(parentHash, depth + 1, blockStore);
        Set<BlockHeader> ancestorsHeaders = getAncestorsHeaders(parentHash, depth + 1, blockStore);
        Set<BlockHeader> ancestorsUncles = getAncestorsUncles(parentHash, depth + 1, blockStore);

        candidateUncles.removeAll(ancestorsAllHeaders);
        candidateUncles.removeAll(ancestorsUncles);

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

    public static Set<Block> getAncestorsBlocks(BlockHash parentHash, int depth, BlockStore blockStore) throws IOException {
        Set<Block> ancestors = new HashSet<>();

        for (int k = 0; k < depth; k++) {
            Block parent = blockStore.getBlock(parentHash);

            if (parent == null)
                return ancestors;

            ancestors.add(parent);

            if (parent.getNumber() == 0)
                break;

            parentHash = parent.getParentHash();
        }

        return ancestors;
    }

    public static Set<BlockHeader> getAncestorsUncles(BlockHash parentHash, int depth, BlockStore blockStore) throws IOException {
        Set<BlockHeader> uncles = new HashSet<>();

        Set<Block> ancestors = getAncestorsBlocks(parentHash, depth, blockStore);

        for (Block ancestor : ancestors)
            uncles.addAll(ancestor.getUncles());

        return uncles;
    }
}

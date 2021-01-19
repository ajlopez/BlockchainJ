package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajlopez on 19/01/2021.
 */
public class BlockUtils {
    private BlockUtils() {

    }

    public static Set<BlockHeader> getAncestorsHeaders(Block block, int depth, BlockStore blockStore) {
        Set<BlockHeader> ancestors = new HashSet<>();

        return ancestors;
    }
}

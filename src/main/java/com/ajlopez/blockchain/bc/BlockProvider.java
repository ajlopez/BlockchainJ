package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 17/07/2019.
 */
public interface BlockProvider {
    Block getBlockByHash(Hash hash);

    Block getBlockByNumber(long number);
}

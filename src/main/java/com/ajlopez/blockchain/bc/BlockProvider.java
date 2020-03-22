package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;

import java.io.IOException;

/**
 * Created by ajlopez on 17/07/2019.
 */
public interface BlockProvider {
    Block getBlockByHash(Hash hash);

    Block getBlockByNumber(long number) throws IOException;
}

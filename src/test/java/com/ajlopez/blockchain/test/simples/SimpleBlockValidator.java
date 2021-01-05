package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.bc.BlockValidator;
import com.ajlopez.blockchain.core.Block;

/**
 * Created by ajlopez on 05/01/2021.
 */
public class SimpleBlockValidator extends BlockValidator {
    public SimpleBlockValidator() {
        super(null);
    }

    @Override
    public boolean isValid(Block block) {
        return true;
    }

    @Override
    public boolean isValid(Block block, Block parent) {
        return true;
    }
}

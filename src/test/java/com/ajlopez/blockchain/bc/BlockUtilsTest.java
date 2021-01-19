package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by ajlopez on 19/01/2021.
 */
public class BlockUtilsTest {
    @Test
    public void getGenesisAncestorsAsEmptySet() {
        Block genesis = GenesisGenerator.generateGenesis();

        Set<BlockHeader> result = BlockUtils.getAncestorsHeaders(genesis, 0, null);
        
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }
}

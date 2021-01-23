package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 23/01/2021.
 */
public class BlockBuilderTest {
    @Test
    public void createBlockWithNumber() {
        BlockBuilder blockBuilder = new BlockBuilder();

        Block result = blockBuilder.number(42).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
    }
}

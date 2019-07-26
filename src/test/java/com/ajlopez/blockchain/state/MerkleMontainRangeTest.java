package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class MerkleMontainRangeTest {
    @Test
    public void addFirstHash() {
        MerkleMontainRange mmr = new MerkleMontainRange();
        Hash hash = FactoryHelper.createRandomHash();

        Hash result = mmr.addHash(hash);

        Assert.assertNotNull(result);
        Assert.assertEquals(hash, result);
    }

    @Test
    public void addTwoHashes() {
        MerkleMontainRange mmr = new MerkleMontainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();

        mmr.addHash(hash1);
        Hash result = mmr.addHash(hash2);

        Assert.assertNotNull(result);
        Assert.assertEquals(HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes())), result);
    }
}

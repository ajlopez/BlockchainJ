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
public class MerkleMountainRangeTest {
    @Test
    public void createEmpty() {
        MerkleMountainRange mmr = new MerkleMountainRange();

        Assert.assertEquals(0, mmr.getCount());
        Assert.assertNull(mmr.getRootHash());
    }

    @Test
    public void addFirstHash() {
        MerkleMountainRange mmr = new MerkleMountainRange();
        Hash hash = FactoryHelper.createRandomHash();

        mmr = mmr.addHash(hash);

        Assert.assertEquals(1, mmr.getCount());

        Hash result = mmr.getRootHash();

        Assert.assertNotNull(result);
        Assert.assertEquals(hash, result);
    }

    @Test
    public void addTwoHashes() {
        MerkleMountainRange mmr = new MerkleMountainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();

        Hash result = mmr.addHash(hash1).addHash(hash2).getRootHash();

        Assert.assertNotNull(result);
        Assert.assertEquals(HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes())), result);
    }

    @Test
    public void addThreeHashes() {
        MerkleMountainRange mmr = new MerkleMountainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();

        Hash result = mmr.addHash(hash1).addHash(hash2).addHash(hash3).getRootHash();

        Assert.assertNotNull(result);
        Hash root12 = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
        Hash root123 = HashUtils.calculateHash(ByteUtils.concatenate(root12.getBytes(), hash3.getBytes()));
        Assert.assertEquals(root123, result);
    }

    @Test
    public void addFourHashes() {
        MerkleMountainRange mmr = new MerkleMountainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();
        Hash hash4 = FactoryHelper.createRandomHash();

        Hash result = mmr.addHash(hash1).addHash(hash2).addHash(hash3).addHash(hash4).getRootHash();

        Assert.assertNotNull(result);
        Hash root12 = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
        Hash root34 = HashUtils.calculateHash(ByteUtils.concatenate(hash3.getBytes(), hash4.getBytes()));
        Hash root1234 = HashUtils.calculateHash(ByteUtils.concatenate(root12.getBytes(), root34.getBytes()));
        Assert.assertEquals(root1234, result);
    }
}

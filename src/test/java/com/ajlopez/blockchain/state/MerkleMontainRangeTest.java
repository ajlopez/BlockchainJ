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

    @Test
    public void addThreeHashes() {
        MerkleMontainRange mmr = new MerkleMontainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();

        mmr.addHash(hash1);
        mmr.addHash(hash2);
        Hash result = mmr.addHash(hash3);

        Assert.assertNotNull(result);
        Hash root12 = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
        Hash root123 = HashUtils.calculateHash(ByteUtils.concatenate(root12.getBytes(), hash3.getBytes()));
        Assert.assertEquals(root123, result);
    }

    @Test
    public void addFourHashes() {
        MerkleMontainRange mmr = new MerkleMontainRange();
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();
        Hash hash4 = FactoryHelper.createRandomHash();

        mmr.addHash(hash1);
        mmr.addHash(hash2);
        mmr.addHash(hash3);
        Hash result = mmr.addHash(hash4);

        Assert.assertNotNull(result);
        Hash root12 = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));
        Hash root34 = HashUtils.calculateHash(ByteUtils.concatenate(hash3.getBytes(), hash4.getBytes()));
        Hash root1234 = HashUtils.calculateHash(ByteUtils.concatenate(root12.getBytes(), root34.getBytes()));
        Assert.assertEquals(root1234, result);
    }
}

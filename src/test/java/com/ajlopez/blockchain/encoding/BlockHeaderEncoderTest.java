package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 23/09/2017.
 */
public class BlockHeaderEncoderTest {
    @Test
    public void encodeDecodeBlockHeader() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header = new BlockHeader(42, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42));

        byte[] encoded = BlockHeaderEncoder.encode(header);

        Assert.assertNotNull(encoded);

        BlockHeader result = BlockHeaderEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(hash, result.getParentHash());
        Assert.assertEquals(header.getHash(), result.getHash());
        Assert.assertEquals(header.getTransactionsRootHash(), result.getTransactionsRootHash());
        Assert.assertEquals(header.getCoinbase(), result.getCoinbase());
        Assert.assertEquals(header.getDifficulty(), result.getDifficulty());
    }

    @Test
    public void encodeDecodeBlockHeaderList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header1 = new BlockHeader(42, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42));
        BlockHeader header2 = new BlockHeader(100, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(100));

        List<BlockHeader> headers = new ArrayList<>();
        headers.add(header1);
        headers.add(header2);

        byte[] encoded = BlockHeaderEncoder.encode(headers);

        Assert.assertNotNull(encoded);

        List<BlockHeader> result = BlockHeaderEncoder.decodeList(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(header1.getHash(), result.get(0).getHash());
        Assert.assertEquals(header2.getHash(), result.get(1).getHash());
    }
}


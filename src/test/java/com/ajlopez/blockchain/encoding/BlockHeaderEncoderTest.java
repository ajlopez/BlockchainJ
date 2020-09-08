package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 23/09/2017.
 */
public class BlockHeaderEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeBlockHeader() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header = new BlockHeader(42, hash, 100, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42), 0, 0);

        byte[] encoded = BlockHeaderEncoder.encode(header);

        Assert.assertNotNull(encoded);

        BlockHeader result = BlockHeaderEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(hash, result.getParentHash());
        Assert.assertEquals(header.getHash(), result.getHash());
        Assert.assertEquals(header.getTransactionsCount(), result.getTransactionsCount());
        Assert.assertEquals(header.getTransactionsRootHash(), result.getTransactionsRootHash());
        Assert.assertEquals(header.getCoinbase(), result.getCoinbase());
        Assert.assertEquals(header.getDifficulty(), result.getDifficulty());
        Assert.assertEquals(header.getNonce(), result.getNonce());
    }

    @Test
    public void encodeDecodeBlockHeaderWithNonce() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header = new BlockHeader(42, hash, 100, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42), 0, 100);

        byte[] encoded = BlockHeaderEncoder.encode(header);

        Assert.assertNotNull(encoded);

        BlockHeader result = BlockHeaderEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(hash, result.getParentHash());
        Assert.assertEquals(header.getHash(), result.getHash());
        Assert.assertEquals(header.getTransactionsCount(), result.getTransactionsCount());
        Assert.assertEquals(header.getTransactionsRootHash(), result.getTransactionsRootHash());
        Assert.assertEquals(header.getCoinbase(), result.getCoinbase());
        Assert.assertEquals(header.getDifficulty(), result.getDifficulty());
        Assert.assertEquals(header.getNonce(), result.getNonce());
    }

    @Test
    public void encodeDecodeBlockHeaderWithReceiptsHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash receiptsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header = new BlockHeader(42, hash, 100, transactionsHash, receiptsHash, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42), 0, 100);

        byte[] encoded = BlockHeaderEncoder.encode(header);

        Assert.assertNotNull(encoded);

        BlockHeader result = BlockHeaderEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(hash, result.getParentHash());
        Assert.assertEquals(header.getHash(), result.getHash());
        Assert.assertEquals(header.getTransactionsCount(), result.getTransactionsCount());
        Assert.assertEquals(header.getTransactionsRootHash(), result.getTransactionsRootHash());
        Assert.assertEquals(header.getReceiptsRootHash(), result.getReceiptsRootHash());
        Assert.assertEquals(header.getCoinbase(), result.getCoinbase());
        Assert.assertEquals(header.getDifficulty(), result.getDifficulty());
        Assert.assertEquals(header.getNonce(), result.getNonce());
    }

    @Test
    public void encodeDecodeBlockHeaderList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header1 = new BlockHeader(42, hash, 0, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42), 0, 0);
        BlockHeader header2 = new BlockHeader(100, hash, 0, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(100), 0, 0);

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

    @Test
    public void decodeInvalidEncodedBlockHeader() {
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(RLP.encode(bytes));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid block header encoding");
        BlockHeaderEncoder.decode(encoded);
    }
}


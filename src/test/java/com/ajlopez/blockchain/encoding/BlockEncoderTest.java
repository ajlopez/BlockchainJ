package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajlopez on 10/10/2017.
 */
public class BlockEncoderTest {
    @Test
    public void encodeDecodeSimpleBlock() {
        Hash parentHash = new Hash();
        Block block = new Block(42, parentHash);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
    }

    @Test
    public void encodeDecodeSimpleBlockWithOneTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        Hash parentHash = new Hash();
        Block block = new Block(new BlockHeader(42, parentHash), txs);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(1, result.getTransactions().size());
        Assert.assertEquals(block.getTransactions().get(0), result.getTransactions().get(0));
    }

    @Test
    public void encodeTwoBlocks() {
        Hash parentHash = new Hash();
        Block block1 = new Block(42, parentHash);
        Block block2 = new Block(0, null);

        byte[] encoded1 = BlockEncoder.encode(block1);
        byte[] encoded2 = BlockEncoder.encode(block2);

        Assert.assertNotNull(encoded1);
        Assert.assertNotNull(encoded2);

        Assert.assertFalse(Arrays.equals(encoded1, encoded2));
    }
}

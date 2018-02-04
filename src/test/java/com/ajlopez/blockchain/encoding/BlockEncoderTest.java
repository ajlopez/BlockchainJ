package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.*;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
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
    public void encodeDecodeBlock() {
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
    public void encodeDecodeBlockWithOneTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 0);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        Hash parentHash = new Hash();
        Block block = new Block(42, parentHash, txs);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(1, result.getTransactions().size());
        Assert.assertEquals(tx.getSender(), result.getTransactions().get(0).getSender());
        Assert.assertEquals(tx.getReceiver(), result.getTransactions().get(0).getReceiver());
        Assert.assertEquals(tx.getValue(), result.getTransactions().get(0).getValue());
        Assert.assertNotEquals(Hash.emptyHash(), result.getTransactions().get(0).getHash());
        Assert.assertEquals(tx.getHash(), result.getTransactions().get(0).getHash());
    }

    @Test
    public void encodeDecodeBlockWithTwoTransactions() {
        Address account1 = new Address();
        Address account2 = new Address();

        Transaction tx1 = new Transaction(account1, account2, BigInteger.ONE, 0);
        Transaction tx2 = new Transaction(account2, account1, BigInteger.TEN, 1);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx1);
        txs.add(tx2);

        Hash parentHash = new Hash();
        Block block = new Block(42, parentHash, txs);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(2, result.getTransactions().size());

        Assert.assertEquals(tx1.getSender(), result.getTransactions().get(0).getSender());
        Assert.assertEquals(tx1.getReceiver(), result.getTransactions().get(0).getReceiver());
        Assert.assertEquals(tx1.getValue(), result.getTransactions().get(0).getValue());
        Assert.assertEquals(tx1.getNonce(), result.getTransactions().get(0).getNonce());
        Assert.assertNotEquals(Hash.emptyHash(), result.getTransactions().get(0).getHash());
        Assert.assertEquals(tx1.getHash(), result.getTransactions().get(0).getHash());

        Assert.assertEquals(tx2.getSender(), result.getTransactions().get(1).getSender());
        Assert.assertEquals(tx2.getReceiver(), result.getTransactions().get(1).getReceiver());
        Assert.assertEquals(tx2.getValue(), result.getTransactions().get(1).getValue());
        Assert.assertEquals(tx2.getNonce(), result.getTransactions().get(1).getNonce());
        Assert.assertNotEquals(Hash.emptyHash(), result.getTransactions().get(1).getHash());
        Assert.assertEquals(tx2.getHash(), result.getTransactions().get(1).getHash());
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

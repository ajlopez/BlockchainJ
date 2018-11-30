package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class MessageEncoderTest {
    @Test
    public void encodeBlockMessage() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block = new Block(1L, hash, HashUtilsTest.generateRandomHash());

        BlockMessage message = new BlockMessage(block);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(0, bytes.length);

        byte[] bblock = BlockEncoder.encode(block);
        int blength = bblock.length;

        Assert.assertEquals(1 + Integer.BYTES + bblock.length, bytes.length);
        Assert.assertEquals(MessageType.BLOCK.ordinal(), bytes[0]);
        Assert.assertTrue(ByteUtils.equals(ByteUtils.unsignedIntegerToBytes(blength), 0, bytes, 1, Integer.BYTES));
    }

    @Test
    public void encodeAndDecodeBlockMessage() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block = new Block(1L, hash, HashUtilsTest.generateRandomHash());

        BlockMessage message = new BlockMessage(block);

        byte[] bytes = MessageEncoder.encode(message);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bresult = (BlockMessage)result;

        Assert.assertEquals(block.getHash(), bresult.getBlock().getHash());
    }

    @Test
    public void encodeGetBlockByNumberMessage() {
        Message message = new GetBlockByNumberMessage(42);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(0, bytes.length);

        Assert.assertEquals(1 + Integer.BYTES + Long.BYTES, bytes.length);
        Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER.ordinal(), bytes[0]);
        Assert.assertTrue(ByteUtils.equals(ByteUtils.unsignedIntegerToBytes(Long.BYTES), 0, bytes, 1, Integer.BYTES));
    }

    @Test
    public void encodeAndDecodeGetBlockByNumberMessage() {
        Message message = new GetBlockByNumberMessage(42);

        byte[] bytes = MessageEncoder.encode(message);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, result.getMessageType());

        GetBlockByNumberMessage bresult = (GetBlockByNumberMessage)result;

        Assert.assertEquals(42, bresult.getNumber());
    }

    @Test
    public void encodeAndDecodeGetBlockByHashMessage() {
        BlockHash hash = HashUtilsTest.generateRandomBlockHash();
        Message message = new GetBlockByHashMessage(hash);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);
        Assert.assertEquals(1 + Integer.BYTES + Hash.HASH_BYTES, bytes.length);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_BLOCK_BY_HASH, result.getMessageType());

        GetBlockByHashMessage bresult = (GetBlockByHashMessage)result;

        Assert.assertEquals(hash, bresult.getHash());
    }

    @Test
    public void encodeAndDecodeTransactionMessage() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42);

        Message message = new TransactionMessage(tx);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.TRANSACTION, result.getMessageType());

        TransactionMessage tresult = (TransactionMessage)result;

        Assert.assertEquals(tx.getHash(), tresult.getTransaction().getHash());
    }

    @Test
    public void encodeAndDecodeStatusMessage() {
        PeerId nodeId = HashUtilsTest.generateRandomPeerId();
        Message message = new StatusMessage(new Status(nodeId, 2, 3));

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.STATUS, result.getMessageType());

        StatusMessage sresult = (StatusMessage)result;

        Assert.assertEquals(nodeId, sresult.getStatus().getPeerId());
        Assert.assertEquals(2, sresult.getStatus().getNetworkNumber());
        Assert.assertEquals(3, sresult.getStatus().getBestBlockNumber());
    }
}

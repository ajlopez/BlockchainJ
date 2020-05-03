package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.Status;
import com.ajlopez.blockchain.store.StoreType;
import com.ajlopez.blockchain.store.TrieType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class MessageEncoderTest {
    @Test
    public void encodeBlockMessage() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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

        Assert.assertEquals(1 + Integer.BYTES + 1, bytes.length);
        Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER.ordinal(), bytes[0]);
        Assert.assertTrue(ByteUtils.equals(ByteUtils.unsignedIntegerToBytes(1), 0, bytes, 1, Integer.BYTES));
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
        BlockHash hash = FactoryHelper.createRandomBlockHash();
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
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 42, null, 6000000, Coin.ZERO);

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
        PeerId nodeId = FactoryHelper.createRandomPeerId();
        Message message = new StatusMessage(new Status(nodeId, 2, 3, FactoryHelper.createRandomBlockHash()));

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

    @Test
    public void encodeAndDecodeGetStatusMessage() {
        Message message = GetStatusMessage.getInstance();

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);
        Assert.assertEquals(1, bytes.length);
        Assert.assertEquals(MessageType.GET_STATUS.ordinal(), bytes[0]);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_STATUS, result.getMessageType());
        Assert.assertSame(GetStatusMessage.getInstance(), result);
    }

    @Test
    public void encodeAndDecodeTrieNodeMessage() {
        Hash topHash = FactoryHelper.createRandomHash();
        byte[] nodeData = FactoryHelper.createRandomBytes(42);

        Message message = new TrieNodeMessage(topHash, TrieType.ACCOUNT, nodeData);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.TRIE_NODE, result.getMessageType());

        TrieNodeMessage tnresult = (TrieNodeMessage)result;

        Assert.assertEquals(topHash, tnresult.getTopHash());
        Assert.assertEquals(TrieType.ACCOUNT, tnresult.getTrieType());
        Assert.assertArrayEquals(nodeData, tnresult.getTrieNode());
    }

    @Test
    public void encodeAndDecodeGetStoredValueMessage() {
        StoreType storeType = StoreType.CONTRACTS;
        byte[] key = FactoryHelper.createRandomBytes(32);

        GetStoredValueMessage message = new GetStoredValueMessage(storeType, key);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_STORED_VALUE, result.getMessageType());

        GetStoredValueMessage gsvresult = (GetStoredValueMessage)result;

        Assert.assertEquals(StoreType.CONTRACTS, gsvresult.getStoreType());
        Assert.assertArrayEquals(key, gsvresult.getKey());
    }

    @Test
    public void encodeAndDecodeStoredKeyValueMessage() {
        StoreType storeType = StoreType.BLOCKS;
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        StoredKeyValueMessage message = new StoredKeyValueMessage(storeType, key, value);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.STORED_KEY_VALUE, result.getMessageType());

        StoredKeyValueMessage skvresult = (StoredKeyValueMessage)result;

        Assert.assertEquals(StoreType.BLOCKS, skvresult.getStoreType());
        Assert.assertArrayEquals(key, skvresult.getKey());
        Assert.assertArrayEquals(value, skvresult.getValue());
    }

    @Test
    public void encodeAndDecodeGetTrieNodeMessage() {
        Hash topHash = FactoryHelper.createRandomHash();
        Hash trieHash = FactoryHelper.createRandomHash();

        Message message = new GetTrieNodeMessage(topHash, TrieType.ACCOUNT, trieHash);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_TRIE_NODE, result.getMessageType());

        GetTrieNodeMessage gtnresult = (GetTrieNodeMessage)result;

        Assert.assertEquals(topHash, gtnresult.getTopHash());
        Assert.assertEquals(TrieType.ACCOUNT, gtnresult.getTrieType());
        Assert.assertEquals(trieHash, gtnresult.getTrieHash());
    }

    @Test
    public void encodeAndDecodeGetBlockHashesMessage() {
        Message message = new GetBlockHashesMessage(42, 10, 2);

        byte[] bytes = MessageEncoder.encode(message);

        Assert.assertNotNull(bytes);

        Message result = MessageEncoder.decode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.GET_BLOCK_HASHES, result.getMessageType());

        GetBlockHashesMessage gbhresult = (GetBlockHashesMessage)result;

        Assert.assertEquals(42, gbhresult.getBlockHeight());
        Assert.assertEquals(10, gbhresult.getNoBlocks());
        Assert.assertEquals(2, gbhresult.getBlockGap());
    }
}

package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.bc.BlockBuilder;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class BlockJsonEncoderTest {
    @Test
    public void encodeBlockWithoutTransactions() throws IOException {
        Block block = FactoryHelper.createBlockChain(1).getBlockByNumber(1);

        JsonValue result = BlockJsonEncoder.encode(block, Difficulty.TWO, false);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("number"));
        Assert.assertTrue(oresult.hasProperty("miner")) ;
        Assert.assertTrue(oresult.hasProperty("parentHash"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("stateRoot"));
        Assert.assertTrue(oresult.hasProperty("difficulty"));
        Assert.assertTrue(oresult.hasProperty("totalDifficulty"));
        Assert.assertTrue(oresult.hasProperty("transactionRoot"));
        Assert.assertTrue(oresult.hasProperty("gasLimit"));
        Assert.assertTrue(oresult.hasProperty("uncles"));
        Assert.assertTrue(oresult.hasProperty("transactions"));
        Assert.assertTrue(oresult.hasProperty("timestamp"));

        Assert.assertEquals(block.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(block.getNumber() + "", oresult.getProperty("number").getValue());
        Assert.assertEquals(block.getCoinbase().toString(), oresult.getProperty("miner").getValue());
        Assert.assertEquals(block.getParentHash().toString(), oresult.getProperty("parentHash").getValue());
        Assert.assertEquals("0", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(block.getStateRootHash().toString(), oresult.getProperty("stateRoot").getValue());
        Assert.assertEquals(block.getTransactionsRootHash().toString(), oresult.getProperty("transactionRoot").getValue());
        Assert.assertEquals(block.getDifficulty().toString(), oresult.getProperty("difficulty").getValue());
        Assert.assertEquals(Difficulty.TWO.toString(), oresult.getProperty("totalDifficulty").getValue());
        Assert.assertEquals(block.getTimestamp() + "", oresult.getProperty("timestamp").getValue());
        Assert.assertEquals(block.getGasLimit() + "", oresult.getProperty("gasLimit").getValue());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("uncles").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("uncles")).size());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("transactions").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("transactions")).size());
    }

    @Test
    public void encodeBlockWithTransactions() throws IOException {
        Block block = FactoryHelper.createBlockChain(1, 10).getBlockByNumber(1);

        Assert.assertEquals(10, block.getTransactions().size());

        JsonValue result = BlockJsonEncoder.encode(block, Difficulty.TWO, false);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("number"));
        Assert.assertTrue(oresult.hasProperty("miner")) ;
        Assert.assertTrue(oresult.hasProperty("parentHash"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("stateRoot"));
        Assert.assertTrue(oresult.hasProperty("difficulty"));
        Assert.assertTrue(oresult.hasProperty("gasLimit"));
        Assert.assertTrue(oresult.hasProperty("totalDifficulty"));
        Assert.assertTrue(oresult.hasProperty("transactionRoot"));
        Assert.assertTrue(oresult.hasProperty("uncles"));
        Assert.assertTrue(oresult.hasProperty("transactions"));
        Assert.assertTrue(oresult.hasProperty("timestamp"));

        Assert.assertEquals(block.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(block.getNumber() + "", oresult.getProperty("number").getValue());
        Assert.assertEquals(block.getCoinbase().toString(), oresult.getProperty("miner").getValue());
        Assert.assertEquals(block.getParentHash().toString(), oresult.getProperty("parentHash").getValue());
        Assert.assertEquals("0", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(block.getStateRootHash().toString(), oresult.getProperty("stateRoot").getValue());
        Assert.assertEquals(block.getTransactionsRootHash().toString(), oresult.getProperty("transactionRoot").getValue());
        Assert.assertEquals(block.getDifficulty().toString(), oresult.getProperty("difficulty").getValue());
        Assert.assertEquals(Difficulty.TWO.toString(), oresult.getProperty("totalDifficulty").getValue());
        Assert.assertEquals(block.getTimestamp() + "", oresult.getProperty("timestamp").getValue());
        Assert.assertEquals(block.getGasLimit() + "", oresult.getProperty("gasLimit").getValue());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("uncles").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("uncles")).size());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("transactions").getType());
        Assert.assertEquals(10, ((JsonArrayValue)oresult.getProperty("transactions")).size());

        List<Transaction> transactions = block.getTransactions();

        for (int k = 0; k < transactions.size(); k++)
            Assert.assertEquals(transactions.get(k).getHash().toString(), ((JsonArrayValue)oresult.getProperty("transactions")).getValue(k).getValue());
    }

    @Test
    public void encodeBlockWithFullTransactions() throws IOException {
        Block block = FactoryHelper.createBlockChain(1, 10).getBlockByNumber(1);

        Assert.assertEquals(10, block.getTransactions().size());

        JsonValue result = BlockJsonEncoder.encode(block, Difficulty.TWO, true);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("number"));
        Assert.assertTrue(oresult.hasProperty("miner")) ;
        Assert.assertTrue(oresult.hasProperty("parentHash"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("stateRoot"));
        Assert.assertTrue(oresult.hasProperty("difficulty"));
        Assert.assertTrue(oresult.hasProperty("gasLimit"));
        Assert.assertTrue(oresult.hasProperty("totalDifficulty"));
        Assert.assertTrue(oresult.hasProperty("transactionRoot"));
        Assert.assertTrue(oresult.hasProperty("uncles"));
        Assert.assertTrue(oresult.hasProperty("transactions"));
        Assert.assertTrue(oresult.hasProperty("timestamp"));

        Assert.assertEquals(block.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(block.getNumber() + "", oresult.getProperty("number").getValue());
        Assert.assertEquals(block.getCoinbase().toString(), oresult.getProperty("miner").getValue());
        Assert.assertEquals(block.getParentHash().toString(), oresult.getProperty("parentHash").getValue());
        Assert.assertEquals("0", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(block.getStateRootHash().toString(), oresult.getProperty("stateRoot").getValue());
        Assert.assertEquals(block.getTransactionsRootHash().toString(), oresult.getProperty("transactionRoot").getValue());
        Assert.assertEquals(block.getDifficulty().toString(), oresult.getProperty("difficulty").getValue());
        Assert.assertEquals(Difficulty.TWO.toString(), oresult.getProperty("totalDifficulty").getValue());
        Assert.assertEquals(block.getTimestamp() + "", oresult.getProperty("timestamp").getValue());
        Assert.assertEquals(block.getGasLimit() + "", oresult.getProperty("gasLimit").getValue());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("uncles").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("uncles")).size());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("transactions").getType());
        Assert.assertEquals(10, ((JsonArrayValue)oresult.getProperty("transactions")).size());

        List<Transaction> transactions = block.getTransactions();

        for (int k = 0; k < transactions.size(); k++)
            Assert.assertEquals(transactions.get(k).getHash().toString(), ((JsonObjectValue)((JsonArrayValue)oresult.getProperty("transactions")).getValue(k)).getProperty("hash").getValue());
    }

    @Test
    public void encodeBlockWithUncles() throws IOException {
        BlockHeader uncle1 = new BlockBuilder().buildHeader();
        BlockHeader uncle2 = new BlockBuilder().buildHeader();
        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

        Block block = new BlockBuilder().uncles(uncles).build();

        Assert.assertEquals(2, block.getUncles().size());

        JsonValue result = BlockJsonEncoder.encode(block, Difficulty.TWO, false);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("number"));
        Assert.assertTrue(oresult.hasProperty("miner")) ;
        Assert.assertTrue(oresult.hasProperty("parentHash"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("stateRoot"));
        Assert.assertTrue(oresult.hasProperty("difficulty"));
        Assert.assertTrue(oresult.hasProperty("gasLimit"));
        Assert.assertTrue(oresult.hasProperty("totalDifficulty"));
        Assert.assertTrue(oresult.hasProperty("transactionRoot"));
        Assert.assertTrue(oresult.hasProperty("uncles"));
        Assert.assertTrue(oresult.hasProperty("transactions"));
        Assert.assertTrue(oresult.hasProperty("timestamp"));

        Assert.assertEquals(block.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(block.getNumber() + "", oresult.getProperty("number").getValue());
        Assert.assertEquals(block.getCoinbase().toString(), oresult.getProperty("miner").getValue());
        Assert.assertEquals(block.getParentHash().toString(), oresult.getProperty("parentHash").getValue());
        Assert.assertEquals("0", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(block.getStateRootHash().toString(), oresult.getProperty("stateRoot").getValue());
        Assert.assertEquals(block.getTransactionsRootHash().toString(), oresult.getProperty("transactionRoot").getValue());
        Assert.assertEquals(block.getDifficulty().toString(), oresult.getProperty("difficulty").getValue());
        Assert.assertEquals(Difficulty.TWO.toString(), oresult.getProperty("totalDifficulty").getValue());
        Assert.assertEquals(block.getTimestamp() + "", oresult.getProperty("timestamp").getValue());
        Assert.assertEquals(block.getGasLimit() + "", oresult.getProperty("gasLimit").getValue());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("uncles").getType());
        Assert.assertEquals(2, ((JsonArrayValue)oresult.getProperty("uncles")).size());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("transactions").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("transactions")).size());

        List<Transaction> transactions = block.getTransactions();

        for (int k = 0; k < transactions.size(); k++)
            Assert.assertEquals(transactions.get(k).getHash().toString(), ((JsonArrayValue)oresult.getProperty("transactions")).getValue(k).getValue());
    }
}

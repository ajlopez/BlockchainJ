package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class BlockJsonEncoderTest {
    @Test
    public void encodeBlockWithoutTransactions() {
        Block block = FactoryHelper.createBlockChain(1).getBlockByNumber(1);

        JsonValue result = BlockJsonEncoder.encode(block);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("number"));
        Assert.assertTrue(oresult.hasProperty("miner")) ;
        Assert.assertTrue(oresult.hasProperty("parentHash"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("stateRoot"));
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
        Assert.assertEquals(block.getTransactionRootHash().toString(), oresult.getProperty("transactionRoot").getValue());
        Assert.assertEquals(block.getHeader().getTimestamp() + "", oresult.getProperty("timestamp").getValue());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("uncles").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("uncles")).size());
        Assert.assertEquals(JsonValueType.ARRAY, oresult.getProperty("transactions").getType());
        Assert.assertEquals(0, ((JsonArrayValue)oresult.getProperty("transactions")).size());
    }
}

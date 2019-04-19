package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Transaction;
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
public class TransactionJsonEncoderTest {
    @Test
    public void encodeTransaction() {
        Transaction transaction = FactoryHelper.createTransaction(1000);

        JsonValue result = TransactionJsonEncoder.encode(transaction);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("from"));
        Assert.assertTrue(oresult.hasProperty("to"));
        Assert.assertTrue(oresult.hasProperty("value"));

        Assert.assertEquals(transaction.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(transaction.getSender().toString(), oresult.getProperty("from").getValue());
        Assert.assertEquals(transaction.getReceiver().toString(), oresult.getProperty("to").getValue());
        Assert.assertEquals(BigInteger.valueOf(1000).toString(), oresult.getProperty("value").getValue());
    }

}

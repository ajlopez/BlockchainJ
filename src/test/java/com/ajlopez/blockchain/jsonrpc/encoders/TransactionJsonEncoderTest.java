package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.json.JsonNullValue;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

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
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("gas"));
        Assert.assertTrue(oresult.hasProperty("gasPrice"));
        Assert.assertFalse(oresult.hasProperty("data"));

        Assert.assertEquals(transaction.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(transaction.getSender().toString(), oresult.getProperty("from").getValue());
        Assert.assertEquals(transaction.getReceiver().toString(), oresult.getProperty("to").getValue());
        Assert.assertEquals(transaction.getValue().toString(), oresult.getProperty("value").getValue());
        Assert.assertEquals(transaction.getNonce() + "", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(transaction.getGas() + "", oresult.getProperty("gas").getValue());
        Assert.assertEquals(transaction.getGasPrice().toString(), oresult.getProperty("gasPrice").getValue());
    }

    @Test
    public void encodeTransactionWithData() {
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, to, value, nonce, data, gas, gasPrice);

        JsonValue result = TransactionJsonEncoder.encode(transaction);

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.OBJECT, result.getType());

        JsonObjectValue oresult = (JsonObjectValue)result;

        Assert.assertTrue(oresult.hasProperty("hash"));
        Assert.assertTrue(oresult.hasProperty("from"));
        Assert.assertTrue(oresult.hasProperty("to"));
        Assert.assertTrue(oresult.hasProperty("value"));
        Assert.assertTrue(oresult.hasProperty("nonce"));
        Assert.assertTrue(oresult.hasProperty("gas"));
        Assert.assertTrue(oresult.hasProperty("gasPrice"));
        Assert.assertTrue(oresult.hasProperty("data"));

        Assert.assertEquals(transaction.getHash().toString(), oresult.getProperty("hash").getValue());
        Assert.assertEquals(transaction.getSender().toString(), oresult.getProperty("from").getValue());
        Assert.assertEquals(transaction.getReceiver().toString(), oresult.getProperty("to").getValue());
        Assert.assertEquals(transaction.getValue().toString(), oresult.getProperty("value").getValue());
        Assert.assertEquals(transaction.getNonce() + "", oresult.getProperty("nonce").getValue());
        Assert.assertEquals(transaction.getGas() + "", oresult.getProperty("gas").getValue());
        Assert.assertEquals(transaction.getGasPrice().toString(), oresult.getProperty("gasPrice").getValue());
        Assert.assertArrayEquals(transaction.getData(), HexUtils.hexStringToBytes(oresult.getProperty("data").getValue().toString()));
    }

    @Test
    public void encodeNullTransaction() {
        Assert.assertEquals(JsonNullValue.getInstance(), TransactionJsonEncoder.encode(null));
    }

    @Test
    public void decodeNullJsonObject() {
        Assert.assertNull(TransactionJsonEncoder.decode(JsonNullValue.getInstance()));
    }

    @Test
    public void encodeDecodeTransaction() {
        Transaction transaction = FactoryHelper.createTransaction(1000);

        JsonValue jsonValue = TransactionJsonEncoder.encode(transaction);

        Transaction result = TransactionJsonEncoder.decode(jsonValue);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertEquals(transaction.getValue(), result.getValue());
        Assert.assertEquals(transaction, result);
    }

    @Test
    public void encodeDecodeTransactionWithData() {
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, to, value, nonce, data, gas, gasPrice);

        JsonValue jsonValue = TransactionJsonEncoder.encode(transaction);

        Transaction result = TransactionJsonEncoder.decode(jsonValue);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertEquals(transaction.getValue(), result.getValue());
        Assert.assertEquals(transaction, result);
    }

    @Test
    public void encodeDecodeTransactionWithoutNonce() {
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, to, value, nonce, data, gas, gasPrice);

        JsonValue jsonValue = removeProperty((JsonObjectValue)TransactionJsonEncoder.encode(transaction), "nonce");

        Transaction result = TransactionJsonEncoder.decode(jsonValue);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertEquals(transaction.getValue(), result.getValue());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeTransactionWithoutSomeProperties() {
        Address from = FactoryHelper.createRandomAddress();
        Address to = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, to, value, nonce, data, gas, gasPrice);

        JsonValue jsonValue = removeProperty(removeProperty(removeProperty(removeProperty((JsonObjectValue)TransactionJsonEncoder.encode(transaction), "nonce"), "gas"), "gasPrice"), "value");

        Transaction result = TransactionJsonEncoder.decode(jsonValue);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertEquals(Coin.ZERO, result.getValue());
        Assert.assertEquals(0L, result.getNonce());
        Assert.assertEquals(Coin.ZERO, result.getGasPrice());
        Assert.assertEquals(0L, result.getGas());
    }

    @Test
    public void encodeDecodeTransactionWithoutReceiver() {
        Address from = FactoryHelper.createRandomAddress();
        Coin value = Coin.fromUnsignedLong(1000);
        Coin gasPrice = Coin.fromUnsignedLong(10000);
        long gas = 100;
        byte[] data = FactoryHelper.createRandomBytes(42);
        long nonce = 17;

        Transaction transaction = new Transaction(from, null, value, nonce, data, gas, gasPrice);

        JsonValue jsonValue = TransactionJsonEncoder.encode(transaction);

        Assert.assertFalse(((JsonObjectValue)jsonValue).hasProperty("to"));

        Transaction result = TransactionJsonEncoder.decode(jsonValue);

        Assert.assertEquals(transaction.getSender(), result.getSender());
        Assert.assertEquals(transaction.getReceiver(), result.getReceiver());
        Assert.assertNull(result.getReceiver());
        Assert.assertEquals(transaction.getValue(), result.getValue());
        Assert.assertEquals(transaction, result);
    }

    public static JsonObjectValue removeProperty(JsonObjectValue jovalue, String toremove) {
        Map<String, JsonValue> newprops = new LinkedHashMap<>();

        for (String name : jovalue.getPropertyNames())
            if (!name.equals(toremove))
                newprops.put(name, jovalue.getProperty(name));

        return new JsonObjectValue(newprops);
    }
}

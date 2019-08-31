package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.json.*;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class TransactionJsonEncoder {
    private TransactionJsonEncoder() {}

    public static JsonValue encode(Transaction transaction) {
        if (transaction == null)
            return JsonNullValue.getInstance();

        JsonObjectBuilder builder = new JsonObjectBuilder(new JsonBuilder());

        builder.name("hash")
            .value(transaction.getHash())
            .name("from")
            .value(transaction.getSender())
            .name("to")
            .value(transaction.getReceiver())
            .name("nonce")
            .value(transaction.getNonce())
            .name("value")
            .value(transaction.getValue())
            .name("gas")
            .value(transaction.getGas())
            .name("gasPrice")
            .value(transaction.getGasPrice());

        byte[] data = transaction.getData();

        if (data != null)
            builder.name("data").value(HexUtils.bytesToHexString(data, true));

        return builder.build();
    }

    public static Transaction decode(JsonValue jsonValue) {
        if (jsonValue.getType() == JsonValueType.NULL)
            return null;

        JsonObjectValue ovalue = (JsonObjectValue)jsonValue;

        Address from = new Address(HexUtils.hexStringToBytes(ovalue.getProperty("from").getValue().toString()));
        Address to = new Address(HexUtils.hexStringToBytes(ovalue.getProperty("to").getValue().toString()));
        Coin value = Coin.fromBytes(HexUtils.hexStringToBytes(ovalue.getProperty("value").getValue().toString()));
        long nonce = Long.parseLong(ovalue.getProperty("nonce").getValue().toString());
        long gas = Long.parseLong(ovalue.getProperty("gas").getValue().toString());
        Coin gasPrice = Coin.fromBytes(HexUtils.hexStringToBytes(ovalue.getProperty("gasPrice").getValue().toString()));

        byte[] data = null;

        if (ovalue.hasProperty("data"))
            data = HexUtils.hexStringToBytes(ovalue.getProperty("data").getValue().toString());

        return new Transaction(from, to, value, nonce, data, gas, gasPrice);
    }
}

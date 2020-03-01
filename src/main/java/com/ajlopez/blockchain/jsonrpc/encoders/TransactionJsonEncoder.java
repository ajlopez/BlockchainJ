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

    // TODO support not include nonce
    public static JsonValue encode(Transaction transaction, boolean includeHash) {
        if (transaction == null)
            return JsonNullValue.getInstance();

        JsonObjectBuilder builder = new JsonObjectBuilder(new JsonBuilder());

        if (includeHash)
            builder.name("hash")
                .value(transaction.getHash());

        builder.name("from")
            .value(transaction.getSender());

        if (transaction.getReceiver() != null)
            builder.name("to")
            .value(transaction.getReceiver());

        builder.name("nonce")
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
        Address to = ovalue.hasProperty("to") ? new Address(HexUtils.hexStringToBytes(ovalue.getProperty("to").getValue().toString())) : null;
        Coin value = ovalue.hasProperty("value") ? Coin.fromBytes(HexUtils.hexStringToBytes(ovalue.getProperty("value").getValue().toString())) : Coin.ZERO;
        long nonce = ovalue.hasProperty("nonce") ? Long.parseLong(ovalue.getProperty("nonce").getValue().toString()) : 0L;
        long gas = ovalue.hasProperty("gas") ? Long.parseLong(ovalue.getProperty("gas").getValue().toString()) : 0L;
        Coin gasPrice = ovalue.hasProperty("gasPrice") ? Coin.fromBytes(HexUtils.hexStringToBytes(ovalue.getProperty("gasPrice").getValue().toString())) : Coin.ZERO;

        byte[] data = null;

        if (ovalue.hasProperty("data"))
            data = HexUtils.hexStringToBytes(ovalue.getProperty("data").getValue().toString());

        return new Transaction(from, to, value, nonce, data, gas, gasPrice);
    }
}

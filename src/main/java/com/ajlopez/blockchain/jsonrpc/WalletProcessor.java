package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonValue;

import java.util.List;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class WalletProcessor extends AbstractJsonRpcProcessor {
    private final List<Address> addresses;

    public WalletProcessor(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        if (request.check("eth_accounts", 0))
            return JsonRpcResponse.createResponse(request, this.addressToJsonArray());

        return super.processRequest(request);
    }

    private JsonValue addressToJsonArray() {
        JsonBuilder builder = new JsonBuilder().array();

        for (Address address : this.addresses)
            builder.value(address.toString());

        return builder.end().build();
    }
}

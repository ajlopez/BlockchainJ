package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.Wallet;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class AccountsProcessor extends AbstractJsonRpcProcessor {
    private final AccountsProvider accountsProvider;
    private final Wallet wallet;

    public AccountsProcessor(AccountsProvider accountsProvider, Wallet wallet) {
        this.accountsProvider = accountsProvider;
        this.wallet = wallet;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        if (request.check("eth_getBalance", 1, 2))
            return getBalance(request);

        if (request.check("eth_getTransactionCount", 1, 2))
            return getTransactionCount(request);

        if (request.check("eth_accounts", 0))
            return getAccounts(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getAccounts(JsonRpcRequest request) {
        List<JsonValue> addresses = new ArrayList<>();

        for (Address address : this.wallet.getAddresses())
            addresses.add(new JsonStringValue(address.toString()));

        JsonArrayValue result = new JsonArrayValue(addresses);

        return JsonRpcResponse.createResponse(request, result);
    }

    private JsonRpcResponse getBalance(JsonRpcRequest request) throws JsonRpcException, IOException {
        Account account = getAccount(request);

        Coin balance = account.getBalance();
        String result = HexUtils.bytesToHexString(balance.toBytes(), true);

        return JsonRpcResponse.createResponse(request, result);
    }

    private JsonRpcResponse getTransactionCount(JsonRpcRequest request) throws JsonRpcException, IOException {
        Account account = getAccount(request);

        return JsonRpcResponse.createResponse(request, account.getNonce());
    }

    private Account getAccount(JsonRpcRequest request) throws JsonRpcException, IOException {
        List<JsonValue> params = request.getParams();

        Address address = new Address(HexUtils.hexStringToBytes(params.get(0).getValue().toString()));

        String blockId = params.size() > 1 ? params.get(1).getValue().toString() : "latest";

        return this.accountsProvider.getAccount(address, blockId);
    }
}

package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.utils.HexUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class AccountsProcessor extends AbstractJsonRpcProcessor {
    private final BlocksProvider blocksProvider;
    private final AccountStoreProvider accountStoreProvider;

    public AccountsProcessor(AccountStoreProvider accountStoreProvider, BlocksProvider blocksProvider) {
        this.accountStoreProvider = accountStoreProvider;
        this.blocksProvider = blocksProvider;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        if (request.check("eth_getBalance", 1, 2))
            return getBalance(request);

        if (request.check("eth_getTransactionCount", 1, 2))
            return getTransactionCount(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getBalance(JsonRpcRequest request) throws JsonRpcException {
        Account account = getAccount(request);

        BigInteger balance = account.getBalance();
        String result = HexUtils.bytesToHexString(balance.toByteArray(), true);

        return JsonRpcResponse.createResponse(request, result);
    }

    private JsonRpcResponse getTransactionCount(JsonRpcRequest request) throws JsonRpcException {
        Account account = getAccount(request);

        return JsonRpcResponse.createResponse(request, account.getNonce());
    }

    private Account getAccount(JsonRpcRequest request) throws JsonRpcException {
        List<JsonValue> params = request.getParams();

        Address address = new Address(HexUtils.hexStringToBytes(params.get(0).getValue().toString()));

        String blockId = params.size() > 1 ? params.get(1).getValue().toString() : "latest";
        Block block = this.blocksProvider.getBlock(blockId);
        Hash hash = block.getStateRootHash();

        AccountStore accountStore = this.accountStoreProvider.retrieve(hash);

        return accountStore.getAccount(address);
    }
}

package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.store.AccountStoreProvider;

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
        request.check("eth_getBalance", 1, 2);
        return super.processRequest(request);
    }
}

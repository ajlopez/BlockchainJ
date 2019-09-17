package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProcessor extends AbstractJsonRpcProcessor {
    private final AccountsProvider accountsProvider;
    private final TransactionsProvider transactionsProvider;

    public TransactionsProcessor(TransactionsProvider transactionsProvider, AccountsProvider accountsProvider) {
        this.transactionsProvider = transactionsProvider;
        this.accountsProvider = accountsProvider;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        if (request.check("eth_getTransactionByHash", 1)) {
            String txid = request.getParams().get(0).getValue().toString();
            Transaction transaction = this.transactionsProvider.getTransaction(txid);
            return JsonRpcResponse.createResponse(request, TransactionJsonEncoder.encode(transaction));
        }

        if (request.check("eth_sendTransaction", 1)) {
            JsonValue jvalue = request.getParams().get(0);
            Transaction transaction = TransactionJsonEncoder.decode(jvalue);
            return JsonRpcResponse.createResponse(request, transaction.getHash().toString());
        }

        return super.processRequest(request);
    }
}

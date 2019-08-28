package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonNullValue;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProcessor extends AbstractJsonRpcProcessor {
    private final TransactionsProvider transactionsProvider;

    public TransactionsProcessor(TransactionsProvider transactionsProvider) {
        this.transactionsProvider = transactionsProvider;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        String txid = request.getParams().get(0).getValue().toString();
        Transaction transaction = this.transactionsProvider.getTransaction(txid);
        return JsonRpcResponse.createResponse(request, TransactionJsonEncoder.encode(transaction));
    }
}

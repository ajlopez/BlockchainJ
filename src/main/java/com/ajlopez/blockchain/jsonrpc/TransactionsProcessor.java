package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonObjectValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.jsonrpc.encoders.TransactionJsonEncoder;
import com.ajlopez.blockchain.processors.TransactionProcessor;

import java.io.IOException;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProcessor extends AbstractJsonRpcProcessor {
    private final AccountsProvider accountsProvider;
    private final TransactionsProvider transactionsProvider;
    private final TransactionProcessor transactionProcessor;

    public TransactionsProcessor(TransactionsProvider transactionsProvider, AccountsProvider accountsProvider, TransactionProcessor transactionProcessor) {
        this.transactionsProvider = transactionsProvider;
        this.accountsProvider = accountsProvider;
        this.transactionProcessor = transactionProcessor;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        if (request.check("eth_getTransactionByHash", 1))
            return getTransactionByHash(request);

        if (request.check("eth_sendTransaction", 1))
            return sendTransaction(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getTransactionByHash(JsonRpcRequest request) {
        String txid = request.getParams().get(0).getValue().toString();
        Transaction transaction = this.transactionsProvider.getTransaction(txid);
        return JsonRpcResponse.createResponse(request, TransactionJsonEncoder.encode(transaction, true, true));
    }

    private JsonRpcResponse sendTransaction(JsonRpcRequest request) throws JsonRpcException, IOException {
        JsonValue jvalue = request.getParams().get(0);
        Transaction transaction = TransactionJsonEncoder.decode(jvalue);

        if (!((JsonObjectValue)jvalue).hasProperty("nonce")) {
            Account sender = this.accountsProvider.getAccount(transaction.getSender(), "latest");
            transaction = transaction.withNonce(this.transactionsProvider.getNextNonce(transaction.getSender(), sender.getNonce()));
        }

        // TODO how to relay the transaction, message/receiver processor instead of transaction processor?
        this.transactionProcessor.processTransaction(transaction);
        return JsonRpcResponse.createResponse(request, transaction.getHash().toString());
    }
}

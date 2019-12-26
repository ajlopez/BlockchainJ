package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.vms.eth.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class TransactionExecutor {
    private final ExecutionContext executionContext;

    public TransactionExecutor(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public List<TransactionResult> executeTransactions(List<Transaction> transactions, BlockData blockData) throws IOException {
        List<TransactionResult> executed = new ArrayList<>();

        for (Transaction transaction : transactions) {
            ExecutionResult executionResult = this.executeTransaction(transaction, blockData);

            if (executionResult != null)
                executed.add(new TransactionResult(transaction, executionResult));
        }

        this.executionContext.commit();

        return executed;
    }

    private ExecutionResult executeTransaction(Transaction transaction, BlockData blockData) throws IOException {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return null;

        Coin senderBalance = this.executionContext.getBalance(sender);
        Coin gasPrice = transaction.getGasPrice();
        Coin gasLimitToPay = gasPrice.multiply(transaction.getGas());

        if (senderBalance.compareTo(transaction.getValue().add(gasLimitToPay)) < 0)
            return null;

        boolean isContractCreation = transaction.isContractCreation();

        ExecutionContext context = this.executionContext.createChildExecutionContext();

        Address receiver = transaction.getReceiver();
        byte[] data = transaction.getData();
        byte[] code = isContractCreation ? data : context.getCode(receiver);

        if (isContractCreation)
            receiver = transaction.getNewContractAddress();

        context.transfer(transaction.getSender(), receiver, transaction.getValue());

        ExecutionResult executionResult;

        if (!ByteUtils.isNullOrEmpty(code))
            executionResult = executeCode(transaction, blockData, sender, isContractCreation, context, receiver, code);
        else
            executionResult = ExecutionResult.OkWithoutData(transaction.getGasCost(), null);

        if (executionResult.wasSuccesful())
            context.commit();
        else
            context.rollback();

        if (!gasPrice.isZero()) {
            Coin gasPayment = gasPrice.multiply(executionResult.getGasUsed());
            this.executionContext.transfer(sender, blockData.getCoinbase(), gasPayment);
        }

        this.executionContext.incrementNonce(transaction.getSender());

        return executionResult;
    }

    // TODO refactor arguments, simplify code
    private ExecutionResult executeCode(Transaction transaction, BlockData blockData, Address sender, boolean isContractCreation, ExecutionContext context, Address receiver, byte[] code) throws IOException {
        long transactionGas = transaction.getGasCost();

        Storage storage = context.getAccountStorage(receiver);
        MessageData messageData = new MessageData(receiver, sender, sender, transaction.getValue(), transaction.getGas() - transactionGas, transaction.getGasPrice(), transaction.getData(), false);
        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, blockData, null);
        VirtualMachine vm = new VirtualMachine(programEnvironment, storage);

        try {
            ExecutionResult executionResult = vm.execute(code);
            executionResult.addGasUsed(transactionGas);

            if (executionResult.wasSuccesful() && isContractCreation)
                context.setCode(receiver, executionResult.getReturnedData());

            return executionResult;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO consider this case
        return null;
    }
}

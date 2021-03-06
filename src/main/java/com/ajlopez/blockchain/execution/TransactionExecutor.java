package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.vms.eth.*;

import javax.xml.crypto.Data;
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

    public List<TransactionReceipt> executeTransactions(List<Transaction> transactions, BlockData blockData) throws IOException {
        List<TransactionReceipt> executed = new ArrayList<>();

        for (Transaction transaction : transactions) {
            ExecutionResult executionResult = this.executeTransaction(transaction, blockData);

            TransactionReceipt transactionReceipt = executionResult == null ? null : new TransactionReceipt(executionResult.getGasUsed(), executionResult.wasSuccesful(), executionResult.getLogs());

            executed.add(transactionReceipt);
        }

        this.executionContext.commit();

        return executed;
    }

    public ExecutionResult executeTransaction(Transaction transaction, BlockData blockData) throws IOException {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return null;

        Coin senderBalance = this.executionContext.getBalance(sender);
        Coin gasPrice = transaction.getGasPrice();
        Coin gasLimitToPay = gasPrice.multiply(transaction.getGas());

        if (senderBalance.compareTo(transaction.getValue().add(gasLimitToPay)) < 0)
            return null;

        if (!gasLimitToPay.isZero())
            this.executionContext.transfer(sender, blockData.getCoinbase(), gasLimitToPay);

        boolean isContractCreation = transaction.isContractCreation();
        boolean isRichTransaction = transaction.isRichTransaction();

        ExecutionContext context = this.executionContext.createChildExecutionContext();

        Address receiver = transaction.getReceiver();
        byte[] data = transaction.getData();
        byte[] code = isContractCreation ? data : (isRichTransaction ? transaction.getData() : context.getCode(receiver));

        if (isContractCreation)
            receiver = transaction.getNewContractAddress();
        else if (isRichTransaction)
            receiver = sender;

        if (!transaction.getValue().isZero())
            context.transfer(transaction.getSender(), receiver, transaction.getValue());

        ExecutionResult executionResult;

        if (!ByteUtils.isNullOrEmpty(code))
            executionResult = executeCode(transaction, blockData, sender, isContractCreation, context, receiver, code);
        else {
            executionResult = ExecutionResult.OkWithoutData(transaction.getGasCost(), null);
            context.commit();
        }

        if (!gasPrice.isZero()) {
            Coin gasPayment = gasPrice.multiply(executionResult.getGasUsed());
            Coin gasReimbursement = gasLimitToPay.subtract(gasPayment);

            if (!gasReimbursement.isZero())
                this.executionContext.transfer(blockData.getCoinbase(), sender, gasReimbursement);
        }

        this.executionContext.incrementNonce(transaction.getSender());

        return executionResult;
    }

    private static boolean isShortDeploy(byte[] code) {
        return code.length == DataWord.DATAWORD_BYTES && ByteUtils.areZero(code, 0, DataWord.DATAWORD_BYTES - Address.ADDRESS_BYTES);
    }

    private ExecutionResult executeShortDeploy(ExecutionContext executionContext, Address address, byte[] code, Transaction transaction) throws IOException {
        byte[] addressBytes = new byte[Address.ADDRESS_BYTES];

        System.arraycopy(code, DataWord.DATAWORD_BYTES - Address.ADDRESS_BYTES, addressBytes, 0, Address.ADDRESS_BYTES);

        Address originalContract = new Address(addressBytes);
        long codeLength = executionContext.getCodeLength(originalContract);
        Hash codeHash = executionContext.getCodeHash(originalContract);

        executionContext.setCodeData(address, codeLength, codeHash);

        // TODO review gas cost
        return ExecutionResult.OkWithoutData(transaction.getGasCost(), null);
    }

    private ExecutionResult executeCode(Transaction transaction, BlockData blockData, Address sender, boolean isContractCreation, ExecutionContext context, Address receiver, byte[] code) throws IOException {
        if (isShortDeploy(code))
            return executeShortDeploy(executionContext, receiver, code, transaction);

        long transactionGas = transaction.getGasCost();

        Storage storage = context.getAccountStorage(receiver);

        // TODO review if subtract transaction gas is needed or not
        // TODO see also the addGasUsed line some lines below
        MessageData messageData = new MessageData(receiver, sender, sender, receiver, transaction.getValue(), transaction.getGas() - transactionGas, transaction.getGasPrice(), transaction.getData(), 0, 0, isContractCreation, false);

        VirtualMachine vm = new VirtualMachine(blockData, messageData, context, storage);

        ExecutionResult executionResult = vm.execute(code);

        // TODO test if gas is enough
        executionResult.addGasUsed(transactionGas);

        return executionResult;
    }
}

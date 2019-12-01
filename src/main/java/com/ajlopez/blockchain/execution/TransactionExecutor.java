package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;
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

    public List<Transaction> executeTransactions(List<Transaction> transactions, BlockData blockData) throws IOException {
        List<Transaction> executed = new ArrayList<>();

        for (Transaction transaction : transactions)
            if (this.executeTransaction(transaction, blockData))
                executed.add(transaction);

        this.executionContext.commit();

        return executed;
    }

    private boolean executeTransaction(Transaction transaction, BlockData blockData) throws IOException {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return false;

        Coin senderBalance = this.executionContext.getBalance(sender);
        Coin gasPrice = transaction.getGasPrice();
        Coin gasLimitToPay = gasPrice.multiply(transaction.getGas());

        if (senderBalance.compareTo(transaction.getValue().add(gasLimitToPay)) < 0)
            return false;

        ExecutionContext context = new ChildExecutionContext(this.executionContext);

        Address receiver = transaction.getReceiver();
        byte[] data = transaction.getData();
        byte[] code = receiver == null ? data : context.getCode(receiver);

        // TODO improve contract creation code detection
        if (receiver == null)
            receiver = HashUtils.calculateNewAddress(sender, transaction.getNonce());

        context.transfer(transaction.getSender(), receiver, transaction.getValue());

        long gasUsed = FeeSchedule.TRANSFER.getValue();

        if (!ByteUtils.isNullOrEmpty(data))
            for (int k = 0; k < data.length; k++)
                if (data[k] == 0)
                    gasUsed += FeeSchedule.DATAZERO.getValue();
                else
                    gasUsed += FeeSchedule.DATANONZERO.getValue();

        if (!ByteUtils.isNullOrEmpty(code)) {
            Storage storage = context.getAccountStorage(receiver);
            MessageData messageData = new MessageData(receiver, sender, sender, transaction.getValue(), transaction.getGas(), transaction.getGasPrice(), transaction.getData(), false);
            ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, blockData, null);
            VirtualMachine vm = new VirtualMachine(programEnvironment, storage);

            try {
                ExecutionResult executionResult = vm.execute(code);
                gasUsed += executionResult.getGasUsed();

                if (transaction.getReceiver() == null)
                    context.setCode(receiver, executionResult.getReturnedData());
            }
            catch (VirtualMachineException ex) {
                // TODO revert all
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!gasPrice.isZero()) {
            Coin gasPayment = gasPrice.multiply(gasUsed);
            context.transfer(sender, blockData.getCoinbase(), gasPayment);
        }

        context.incrementNonce(transaction.getSender());

        // TODO apply gas limit gas price
        // TODO apply data cost
        // TODO apply contract creation cost

        context.commit();

        return true;
    }
}

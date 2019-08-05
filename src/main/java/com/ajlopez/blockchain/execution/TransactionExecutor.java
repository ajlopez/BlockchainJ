package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.vms.eth.*;

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

    public List<Transaction> executeTransactions(List<Transaction> transactions, BlockData blockData) {
        List<Transaction> executed = new ArrayList<>();

        for (Transaction transaction : transactions)
            if (this.executeTransaction(transaction, blockData))
                executed.add(transaction);

        this.executionContext.commit();

        return executed;
    }

    private boolean executeTransaction(Transaction transaction, BlockData blockData) {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return false;

        Coin senderBalance = this.executionContext.getBalance(sender);
        Coin gasPrice = transaction.getGasPrice();
        Coin gasLimitToPay = gasPrice.multiply(transaction.getGas());

        if (senderBalance.compareTo(transaction.getValue().add(gasLimitToPay)) < 0)
            return false;

        ExecutionContext context = new ChildExecutionContext(this.executionContext);

        context.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getValue());

        Address receiver = transaction.getReceiver();
        byte[] code = context.getCode(receiver);

        long gasUsed = FeeSchedule.TRANSFER.getValue();

        if (!ByteUtils.isNullOrEmpty(code)) {
            Storage storage = context.getAccountStorage(receiver);
            MessageData messageData = new MessageData(receiver, sender, sender, DataWord.fromCoin(transaction.getValue()), 6000000, DataWord.ZERO, null, false);
            ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);
            VirtualMachine vm = new VirtualMachine(programEnvironment, storage);

            try {
                vm.execute(code);
                gasUsed += vm.getGasUsed();
            }
            catch (VirtualMachineException ex) {
                // TODO revert all
                return false;
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

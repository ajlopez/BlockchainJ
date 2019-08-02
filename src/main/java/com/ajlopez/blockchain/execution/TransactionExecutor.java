package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.vms.eth.*;

import java.math.BigInteger;
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

    public List<Transaction> executeTransactions(List<Transaction> transactions) {
        List<Transaction> executed = new ArrayList<>();

        for (Transaction transaction : transactions)
            if (this.executeTransaction(transaction))
                executed.add(transaction);

        this.executionContext.commit();

        return executed;
    }

    private boolean executeTransaction(Transaction transaction) {
        Address sender = transaction.getSender();

        if (transaction.getNonce() != this.executionContext.getNonce(sender))
            return false;

        BigInteger senderBalance = this.executionContext.getBalance(sender);

        if (senderBalance.compareTo(transaction.getValue()) < 0)
            return false;

        ExecutionContext context = new ChildExecutionContext(this.executionContext);

        context.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getValue());

        Address receiver = transaction.getReceiver();
        byte[] code = context.getCode(receiver);

        if (!ByteUtils.isNullOrEmpty(code)) {
            Storage storage = context.getAccountStorage(receiver);
            ProgramEnvironment programEnvironment = new ProgramEnvironment(new MessageData(null, null, null, null, 6000000, null, null, false), null, null);
            VirtualMachine vm = new VirtualMachine(programEnvironment, storage);

            try {
                vm.execute(code);
                storage.commit();
            }
            catch (VirtualMachineException ex) {
                // TODO revert all
                return false;
            }
        }

        context.incrementNonce(transaction.getSender());

        // TODO apply gas limit gas price
        // TODO apply data cost
        // TODO apply contract creation cost

        context.commit();

        return true;
    }
}

package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStore;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class ExecutionContext {
    private AccountStore accountStore;
    private Map<Address, AccountState> accountStates = new HashMap<>();

    public ExecutionContext(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public void transfer(Address senderAddress, Address receiverAddress, BigInteger amount) {
        AccountState sender = this.getAccountState(senderAddress);
        AccountState receiver = this.getAccountState(receiverAddress);

        sender.subtractFromBalance(amount);
        receiver.addToBalance(amount);
    }

    public BigInteger getBalance(Address address) {
        return this.getAccountState(address).getBalance();
    }

    public void commit() {
        for (Map.Entry<Address, AccountState> entry : this.accountStates.entrySet()) {
            Address address = entry.getKey();
            AccountState accountState = entry.getValue();

            this.accountStore.putAccount(address, accountState.toAccount());
        }

        this.accountStates.clear();
    }

    private AccountState getAccountState(Address address) {
        if (this.accountStates.containsKey(address))
            return this.accountStates.get(address);

        AccountState accountState = AccountState.fromAccount(this.accountStore.getAccount(address));

        this.accountStates.put(address, accountState);

        return accountState;
    }
}

package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ajlopez on 27/11/2018.
 */
public abstract class AbstractExecutionContext implements ExecutionContext {
    private final Map<Address, AccountState> accountStates = new HashMap<>();
    private final Map<Address, Storage> accountStorages = new HashMap<>();
    private final Map<Hash, byte[]> codes = new HashMap<>();
    private final Set<Hash> newCodes = new HashSet<>();

    @Override
    public void transfer(Address senderAddress, Address receiverAddress, Coin amount) throws IOException {
        AccountState sender = this.getAccountState(senderAddress);
        AccountState receiver = this.getAccountState(receiverAddress);

        sender.subtractFromBalance(amount);
        receiver.addToBalance(amount);
    }

    @Override
    public void incrementNonce(Address address) throws IOException {
        AccountState accountState = this.getAccountState(address);

        accountState.incrementNonce();
    }

    @Override
    public Coin getBalance(Address address) throws IOException {
        return this.getAccountState(address).getBalance();
    }

    @Override
    public long getNonce(Address address) throws IOException {
        return this.getAccountState(address).getNonce();
    }

    @Override
    public Hash getCodeHash(Address address) throws IOException { return this.getAccountState(address).getCodeHash(); }

    @Override
    public void commit() throws IOException {
        for (Hash hash : this.newCodes)
            this.updateCode(hash, this.codes.get(hash));

        for (Map.Entry<Address, Storage> entry : this.accountStorages.entrySet()) {
            Storage storage = entry.getValue();
            storage.commit();

            // TODO Improve
            if (storage instanceof TrieStorage) {
                Address address = entry.getKey();
                this.getAccountState(address).setStorageHash(((TrieStorage)storage).getRootHash());
            }
        }

        for (Map.Entry<Address, AccountState> entry : this.accountStates.entrySet()) {
            Address address = entry.getKey();
            AccountState accountState = entry.getValue();

            if (!accountState.wasChanged())
                continue;

            this.updateAccountState(address, accountState);
        }

        this.accountStorages.clear();
        this.accountStates.clear();
        this.codes.clear();
        this.newCodes.clear();
    }

    @Override
    public void rollback() {
        this.accountStorages.clear();
        this.accountStates.clear();
        this.codes.clear();
        this.newCodes.clear();
    }

    public AccountState getAccountState(Address address) throws IOException {
        if (this.accountStates.containsKey(address))
            return this.accountStates.get(address);

        AccountState accountState = this.retrieveAccountState(address);

        if (accountState == null)
            accountState = new AccountState();

        this.setAccountState(address, accountState);

        return this.accountStates.get(address);
    }

    public void setAccountState(Address address, AccountState accountState) {
        this.accountStates.put(address, accountState.cloneState());
    }

    @Override
    public Storage getAccountStorage(Address address) throws IOException {
        if (this.accountStorages.containsKey(address))
            return this.accountStorages.get(address);

        Storage storage = this.retrieveAccountStorage(address);

        this.accountStorages.put(address, storage);

        return storage;
    }

    @Override
    public byte[] getCode(Address address) throws IOException {
        AccountState accountState = this.getAccountState(address);

        Hash codeHash = accountState.getCodeHash();

        return this.getCode(codeHash);
    }

    public byte[] getCode(Hash codeHash) throws IOException {
        if (codeHash == null)
            return null;

        if (this.codes.containsKey(codeHash))
            return this.codes.get(codeHash);

        byte[] code = this.retrieveCode(codeHash);

        this.codes.put(codeHash, code);

        return code;
    }

    abstract AccountState retrieveAccountState(Address address) throws IOException;

    abstract void updateAccountState(Address address, AccountState accountState);

    abstract void updateCode(Hash hash, byte[] code) throws IOException;

    abstract public Storage retrieveAccountStorage(Address address) throws IOException;

    abstract public byte[] retrieveCode(Hash hash) throws IOException;

    @Override
    public ExecutionContext createChildExecutionContext() {
        return new ChildExecutionContext(this);
    }

    @Override
    public void setCode(Address address, byte[] code) throws IOException {
        AccountState accountState = this.getAccountState(address);
        Hash codeHash = HashUtils.calculateHash(code);

        this.setCode(codeHash, code);

        accountState.setCodeHash(codeHash);
    }

    public void setCode(Hash hash, byte[] code) {
        this.codes.put(hash, code);
        this.newCodes.add(hash);
    }
}

package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountState {
    private Coin balance;
    private long nonce;
    private Hash codeHash;
    private Hash storageHash;

    private boolean changed;

    public static AccountState fromAccount(Account account) {
        return new AccountState(account.getBalance(), account.getNonce(), account.getCodeHash(), account.getStorageHash());
    }

    public AccountState() {
        this(Coin.ZERO, 0, null, null);
    }

    public AccountState(Coin balance, long nonce, Hash codeHash, Hash storageHash) {
        if (balance == null)
            balance = Coin.ZERO;

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in account state");

        this.balance = balance;
        this.nonce = nonce;
        this.codeHash = codeHash;
        this.storageHash = normalizeStorageHash(storageHash);
    }

    public Coin getBalance() {
        return this.balance;
    }

    public long getNonce() { return this.nonce; }

    public void incrementNonce() {
        this.nonce++;
        this.changed = true;
    }

    public void addToBalance(Coin amount) {
        if (amount.equals(Coin.ZERO))
            return;

        Coin newbalance = this.balance.add(amount);

        this.balance = newbalance;
        this.changed = true;
    }

    public void subtractFromBalance(Coin amount) {
        if (amount.equals(Coin.ZERO))
            return;

        Coin newbalance = this.balance.subtract(amount);

        this.balance = newbalance;
        this.changed = true;
    }

    public Hash getCodeHash() {
        return this.codeHash;
    }

    public void setCodeHash(Hash codeHash) {
        if (this.codeHash == null && codeHash == null)
            return;

        if (this.codeHash != null && this.codeHash.equals(codeHash))
            return;

        if (this.codeHash != null)
            throw new UnsupportedOperationException("Cannot change code hash");

        this.codeHash = codeHash;
        this.changed = true;
    }

    public Hash getStorageHash() { return this.storageHash; }

    public void setStorageHash(Hash storageHash) {
        Hash normalizedHash = normalizeStorageHash(storageHash);

        if (normalizedHash == null && this.storageHash == null)
            return;

        if (normalizedHash != null && normalizedHash.equals(this.storageHash))
            return;

        this.storageHash = normalizedHash;
        this.changed = true;
    }

    private static Hash normalizeStorageHash(Hash hash) {
        return Trie.EMPTY_TRIE_HASH.equals(hash) ? null : hash;
    }

    public Account toAccount() {
        return new Account(this.balance, this.nonce, 0, this.codeHash, this.storageHash);
    }

    public boolean wasChanged() {
        return this.changed;
    }

    public AccountState cloneState() {
        AccountState clonedState = new AccountState(this.balance, this.nonce, this.codeHash, this.storageHash);

        clonedState.changed = this.changed;

        return clonedState;
    }
}

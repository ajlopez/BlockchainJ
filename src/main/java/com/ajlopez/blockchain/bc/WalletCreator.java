package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.store.AccountStore;

import java.io.IOException;
import java.util.Random;

/**
 * Created by ajlopez on 24/03/2021.
 */
public class WalletCreator {
    private static Random random = new Random();

    private final AccountStore accountStore;

    public WalletCreator(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public Wallet createWallet(int naccounts, Coin balance) throws IOException {
        Wallet wallet = new Wallet();

        for (int k = 0; k < naccounts; k++) {
            Address address = generateRandomAddress();
            Account account = new Account(balance, 0, 0, null, null);
            this.accountStore.putAccount(address, account);
            wallet.addAddress(address);
        }

        return wallet;
    }

    private Address generateRandomAddress() {
        byte[] bytes = new byte[Address.ADDRESS_BYTES];
        random.nextBytes(bytes);
        return new Address(bytes);
    }
}

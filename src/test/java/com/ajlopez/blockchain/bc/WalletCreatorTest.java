package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 24/03/2021.
 */
public class WalletCreatorTest {
    @Test
    public void createWallet() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        WalletCreator walletCreator = new WalletCreator(accountStore);

        Wallet wallet = walletCreator.createWallet(10, Coin.TEN);

        Assert.assertNotNull(wallet);
        Assert.assertEquals(10, wallet.getAddresses().size());

        for (int k = 0; k < 10; k++) {
            Account account = accountStore.getAccount(wallet.getAddresses().get(k));

            Assert.assertNotNull(account);
            Assert.assertEquals(Coin.TEN, account.getBalance());
        }
    }
}

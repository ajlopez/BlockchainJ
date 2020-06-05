package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountTest {
    @Test
    public void createWithZeroBalanceAndZeroNonce() {
        Account account = new Account();

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(0, account.getNonce());
        Assert.assertNull(account.getCodeHash());
        Assert.assertNull(account.getStorageHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonce() {
        Account account = new Account(null, 42, 0, null, null);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertNull(account.getCodeHash());
    }

    @Test
    public void createWithCodeHashAndLength() {
        byte[] code = FactoryHelper.createRandomBytes(100);
        Hash codeHash = HashUtils.calculateHash(code);
        Account account = new Account(null, 42, code.length, codeHash, null);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertEquals(code.length, account.getCodeLength());
        Assert.assertEquals(codeHash, account.getCodeHash());
    }

    @Test
    public void createWithStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();
        Account account = new Account(null, 42, 0, null, storageHash);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertEquals(storageHash, account.getStorageHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new Account(Coin.TEN, -1, 0, null, null);
    }
}

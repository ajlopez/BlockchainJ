package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStateTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createWithZeroBalanceAndZeroNonceAndNullCodeHash() {
        AccountState accountState = new AccountState();

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void setCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState();

        accountState.setCodeHash(codeHash);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertTrue(accountState.wasChanged());
    }


    @Test
    public void setStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState();

        accountState.setStorageHash(storageHash);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void createWithStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.ZERO, 0, null, storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());

        accountState.setStorageHash(storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithEmptyStorageHash() {
        Hash storageHash = Trie.EMPTY_TRIE_HASH;

        AccountState accountState = new AccountState(Coin.ZERO, 0, null, storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithoutStorageHashAndChangeToNull() {
        AccountState accountState = new AccountState();

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());

        accountState.setStorageHash(null);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithoutStorageHashAndChangeToEmptyTrieHash() {
        AccountState accountState = new AccountState();

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());

        accountState.setStorageHash(Trie.EMPTY_TRIE_HASH);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertNull(accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithStorageHashAndChangeIt() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.ZERO, 0, null, storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());

        Hash storageHash2 = FactoryHelper.createRandomHash();

        accountState.setStorageHash(storageHash2);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash2, accountState.getStorageHash());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void cannotSetCodeHashTwiceWithDifferenteValues() {
        Hash codeHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState();

        accountState.setCodeHash(codeHash);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Cannot change code hash");
        accountState.setCodeHash(FactoryHelper.createRandomHash());
    }

    @Test
    public void setCodeHashTwiceWithSameValue() {
        Hash codeHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState();

        accountState.setCodeHash(codeHash);
        accountState.setCodeHash(codeHash);

        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void setSameCodeHashAfterCreation() {
        Hash codeHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.ZERO, 0, codeHash, null);

        accountState.setCodeHash(codeHash);

        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void setSameNullCodeHashAfterCreation() {
        AccountState accountState = new AccountState();

        accountState.setCodeHash(null);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void cannotChangeCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.ZERO, 0, codeHash, null);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Cannot change code hash");
        accountState.setCodeHash(FactoryHelper.createRandomHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonceAndNullCodeHash() {
        AccountState accountState = new AccountState(null, 42, null, null);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(42, accountState.getNonce());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithNullBalanceAndZeroNonceAndCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(null, 0, codeHash, null);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void addToBalance() {
        AccountState accountState = new AccountState();

        accountState.addToBalance(Coin.TEN);
        Assert.assertEquals(Coin.TEN, accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonce() {
        AccountState accountState = new AccountState(Coin.TEN, 42, null, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertNull(result.getCodeHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAndCodeAndStorageHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.TEN, 42, codeHash, storageHash);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(codeHash, result.getCodeHash());
        Assert.assertEquals(storageHash, result.getStorageHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAfterIncrementNonce() {
        AccountState accountState = new AccountState(Coin.TEN, 41, null, null);

        accountState.incrementNonce();

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertNull(result.getCodeHash());
        Assert.assertTrue(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAndCodeHashNonce() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(Coin.TEN, 42, codeHash, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(codeHash, result.getCodeHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void addZeroToBalance() {
        AccountState accountState = new AccountState();

        accountState.addToBalance(Coin.ZERO);
        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void incrementNonce() {
        AccountState accountState = new AccountState();

        Assert.assertEquals(0, accountState.getNonce());
        accountState.incrementNonce();
        Assert.assertTrue(accountState.wasChanged());

        Assert.assertEquals(1, accountState.getNonce());
        accountState.incrementNonce();
        Assert.assertTrue(accountState.wasChanged());

        Assert.assertEquals(2, accountState.getNonce());
        accountState.incrementNonce();
        Assert.assertEquals(3, accountState.getNonce());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void addToAndSubtractFromBalance() {
        AccountState accountState = new AccountState();

        accountState.addToBalance(Coin.TEN);
        Assert.assertTrue(accountState.wasChanged());

        accountState.subtractFromBalance(Coin.ONE);
        Assert.assertEquals(Coin.fromUnsignedLong(9), accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractFromBalance() {
        AccountState accountState = new AccountState(Coin.TEN, 42, null, null);

        accountState.subtractFromBalance(Coin.ONE);
        Assert.assertEquals(Coin.fromUnsignedLong(9), accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractZeroFromBalance() {
        AccountState accountState = new AccountState(Coin.TEN, 42, null, null);

        accountState.subtractFromBalance(Coin.ZERO);
        Assert.assertEquals(Coin.TEN, accountState.getBalance());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new AccountState(Coin.TEN, -1, null, null);
    }

    @Test(expected = ArithmeticException.class)
    public void subtractAmountFromZeroBalance() {
        AccountState accstate = new AccountState();

        accstate.subtractFromBalance(Coin.TEN);
    }

    @Test
    public void createFromAccount() {
        Account account = new Account(Coin.TEN, 42, null, null);

        AccountState result = AccountState.fromAccount(account);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void toAccount() {
        Account account = new Account(Coin.TEN, 42, FactoryHelper.createRandomHash(), null);

        Account result = AccountState.fromAccount(account).toAccount();

        Assert.assertNotNull(result);
        Assert.assertEquals(account.getBalance(), result.getBalance());
        Assert.assertEquals(account.getNonce(), result.getNonce());
        Assert.assertEquals(account.getCodeHash(), result.getCodeHash());
    }
}

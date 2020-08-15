package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
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
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState();

        accountState.setCodeData(code.length, codeHash);

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

        AccountState accountState = new AccountState(Coin.ZERO, 0, 0, null, storageHash);

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

        AccountState accountState = new AccountState(Coin.ZERO, 0, 0, null, storageHash);

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

        AccountState accountState = new AccountState(Coin.ZERO, 0, 0, null, storageHash);

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
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState();

        accountState.setCodeData(code.length, codeHash);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Cannot change code hash");
        accountState.setCodeData(100, FactoryHelper.createRandomHash());
    }

    @Test
    public void setCodeHashTwiceWithSameValue() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState();

        accountState.setCodeData(code.length, codeHash);
        accountState.setCodeData(code.length, codeHash);

        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void setSameCodeHashAfterCreation() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState(Coin.ZERO, 0, code.length, codeHash, null);

        accountState.setCodeData(code.length, codeHash);

        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void setSameNullCodeHashAfterCreation() {
        AccountState accountState = new AccountState();

        accountState.setCodeData(0, null);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void cannotChangeCodeData() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState(Coin.ZERO, 0, code.length, codeHash, null);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Cannot change code hash");
        accountState.setCodeData(100, FactoryHelper.createRandomHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonceAndNullCodeHash() {
        AccountState accountState = new AccountState(null, 42, 0, null, null);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(42, accountState.getNonce());
        Assert.assertEquals(0, accountState.getCodeLength());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithNullBalanceAndZeroNonceAndCodeHash() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);

        AccountState accountState = new AccountState(null, 0, code.length, codeHash, null);

        Assert.assertEquals(Coin.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertEquals(code.length, accountState.getCodeLength());
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
        AccountState accountState = new AccountState(Coin.TEN, 42, 0, null, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(0, result.getCodeLength());
        Assert.assertNull(result.getCodeHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAndCodeAndStorageHash() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(Coin.TEN, 42, code.length, codeHash, storageHash);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(code.length, result.getCodeLength());
        Assert.assertEquals(codeHash, result.getCodeHash());
        Assert.assertEquals(storageHash, result.getStorageHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAfterIncrementNonce() {
        AccountState accountState = new AccountState(Coin.TEN, 41, 0, null, null);

        accountState.incrementNonce();

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(0, result.getCodeLength());
        Assert.assertNull(result.getCodeHash());
        Assert.assertTrue(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAndCodeHashNonce() {
        byte[] code = FactoryHelper.createRandomBytes(42);
        Hash codeHash = HashUtils.calculateHash(code);
        AccountState accountState = new AccountState(Coin.TEN, 42, code.length, codeHash, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(code.length, result.getCodeLength());
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
        AccountState accountState = new AccountState(Coin.TEN, 42, 0, null, null);

        accountState.subtractFromBalance(Coin.ONE);
        Assert.assertEquals(Coin.fromUnsignedLong(9), accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractZeroFromBalance() {
        AccountState accountState = new AccountState(Coin.TEN, 42, 0, null, null);

        accountState.subtractFromBalance(Coin.ZERO);
        Assert.assertEquals(Coin.TEN, accountState.getBalance());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new AccountState(Coin.TEN, -1, 0, null, null);
    }

    @Test(expected = ArithmeticException.class)
    public void subtractAmountFromZeroBalance() {
        AccountState accstate = new AccountState();

        accstate.subtractFromBalance(Coin.TEN);
    }

    @Test
    public void createFromAccount() {
        Account account = new Account(Coin.TEN, 42, 0, null, null);

        AccountState result = AccountState.fromAccount(account);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void toAccount() {
        Account account = new Account(Coin.TEN, 42, 100, FactoryHelper.createRandomHash(), null);

        Account result = AccountState.fromAccount(account).toAccount();

        Assert.assertNotNull(result);
        Assert.assertEquals(account.getBalance(), result.getBalance());
        Assert.assertEquals(account.getNonce(), result.getNonce());
        Assert.assertEquals(account.getCodeLength(), result.getCodeLength());
        Assert.assertEquals(account.getCodeHash(), result.getCodeHash());
    }
}

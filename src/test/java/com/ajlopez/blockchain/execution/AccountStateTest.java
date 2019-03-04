package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

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

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
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

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertTrue(accountState.wasChanged());
    }


    @Test
    public void setStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState();

        accountState.setStorageHash(storageHash);

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void createWithStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(BigInteger.ZERO, 0, null, storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
        Assert.assertFalse(accountState.wasChanged());

        accountState.setStorageHash(storageHash);

        Assert.assertNull(accountState.getCodeHash());
        Assert.assertEquals(storageHash, accountState.getStorageHash());
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
    public void createWithStorageHashAndChangeIt() {
        Hash storageHash = FactoryHelper.createRandomHash();

        AccountState accountState = new AccountState(BigInteger.ZERO, 0, null, storageHash);

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

        AccountState accountState = new AccountState(BigInteger.ZERO, 0, codeHash, null);

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

        AccountState accountState = new AccountState(BigInteger.ZERO, 0, codeHash, null);

        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Cannot change code hash");
        accountState.setCodeHash(FactoryHelper.createRandomHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonceAndNullCodeHash() {
        AccountState accountState = new AccountState(null, 42, null, null);

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(42, accountState.getNonce());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithNullBalanceAndZeroNonceAndCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(null, 0, codeHash, null);

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertEquals(codeHash, accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void addToBalance() {
        AccountState accountState = new AccountState();

        accountState.addToBalance(BigInteger.TEN);
        Assert.assertEquals(BigInteger.TEN, accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonce() {
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertNull(result.getCodeHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAfterIncrementNonce() {
        AccountState accountState = new AccountState(BigInteger.TEN, 41, null, null);

        accountState.incrementNonce();

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertNull(result.getCodeHash());
        Assert.assertTrue(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAndCodeHashNonce() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(BigInteger.TEN, 42, codeHash, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(codeHash, result.getCodeHash());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void addZeroToBalance() {
        AccountState accountState = new AccountState();

        accountState.addToBalance(BigInteger.ZERO);
        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
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

        accountState.addToBalance(BigInteger.TEN);
        Assert.assertTrue(accountState.wasChanged());

        accountState.subtractFromBalance(BigInteger.ONE);
        Assert.assertEquals(9, accountState.getBalance().intValue());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractFromBalance() {
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null, null);

        accountState.subtractFromBalance(BigInteger.ONE);
        Assert.assertEquals(BigInteger.valueOf(9), accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractZeroFromBalance() {
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null, null);

        accountState.subtractFromBalance(BigInteger.ZERO);
        Assert.assertEquals(BigInteger.TEN, accountState.getBalance());
        Assert.assertNull(accountState.getCodeHash());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeBalance() {
        new AccountState(BigInteger.TEN.negate(), 0, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new AccountState(BigInteger.TEN, -1, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void addNegativeNumberToZeroBalance() {
        AccountState accstate = new AccountState();

        accstate.addToBalance(BigInteger.TEN.negate());
    }

    @Test(expected = IllegalStateException.class)
    public void subtractAmountFromZeroBalance() {
        AccountState accstate = new AccountState();

        accstate.subtractFromBalance(BigInteger.TEN);
    }

    @Test
    public void createFromAccount() {
        Account account = new Account(BigInteger.TEN, 42, null, null);

        AccountState result = AccountState.fromAccount(account);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void toAccount() {
        Account account = new Account(BigInteger.TEN, 42, FactoryHelper.createRandomHash(), null);

        Account result = AccountState.fromAccount(account).toAccount();

        Assert.assertNotNull(result);
        Assert.assertEquals(account.getBalance(), result.getBalance());
        Assert.assertEquals(account.getNonce(), result.getNonce());
        Assert.assertEquals(account.getCodeHash(), result.getCodeHash());
    }
}

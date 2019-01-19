package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStateTest {
    @Test
    public void createWithZeroBalanceAndZeroNonce() {
        AccountState accountState = new AccountState();

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(0, accountState.getNonce());
        Assert.assertFalse(accountState.wasChanged());
        Assert.assertNull(accountState.getCodeHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonce() {
        AccountState accountState = new AccountState(null, 42, null);

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(42, accountState.getNonce());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test
    public void createWithCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(null, 42, codeHash);

        Assert.assertEquals(BigInteger.ZERO, accountState.getBalance());
        Assert.assertEquals(42, accountState.getNonce());
        Assert.assertFalse(accountState.wasChanged());
        Assert.assertEquals(codeHash, accountState.getCodeHash());
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
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null);

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertFalse(result.wasChanged());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAfterIncrementNonce() {
        AccountState accountState = new AccountState(BigInteger.TEN, 41, null);

        accountState.incrementNonce();

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertTrue(result.wasChanged());
        Assert.assertEquals(null, result.getCodeHash());
    }

    @Test
    public void cloneWithInitialBalanceAndNonceAfterIncrementNonceAndCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        AccountState accountState = new AccountState(BigInteger.TEN, 41, codeHash);

        accountState.incrementNonce();

        AccountState result = accountState.cloneState();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertTrue(result.wasChanged());
        Assert.assertEquals(codeHash, result.getCodeHash());
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
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null);

        accountState.subtractFromBalance(BigInteger.ONE);
        Assert.assertEquals(BigInteger.valueOf(9), accountState.getBalance());
        Assert.assertTrue(accountState.wasChanged());
    }

    @Test
    public void subtractZeroFromBalance() {
        AccountState accountState = new AccountState(BigInteger.TEN, 42, null);

        accountState.subtractFromBalance(BigInteger.ZERO);
        Assert.assertEquals(BigInteger.TEN, accountState.getBalance());
        Assert.assertFalse(accountState.wasChanged());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeBalance() {
        new AccountState(BigInteger.TEN.negate(), 0, null);
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new AccountState(BigInteger.TEN, -1, null);
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
        Account account = new Account(BigInteger.TEN, 42);

        AccountState result = AccountState.fromAccount(account);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertFalse(result.wasChanged());
    }
}

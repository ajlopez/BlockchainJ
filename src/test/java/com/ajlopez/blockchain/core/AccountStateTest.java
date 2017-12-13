package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountStateTest {
    @Test
    public void createWithZeroBalanceAndZeroNonce() {
        AccountState accstate = new AccountState();

        Assert.assertEquals(BigInteger.ZERO, accstate.getBalance());
        Assert.assertEquals(0, accstate.getNonce());
    }

    @Test
    public void addToBalance() {
        AccountState accstate = new AccountState();

        accstate.addToBalance(BigInteger.TEN);
        Assert.assertEquals(BigInteger.TEN, accstate.getBalance());
    }

    @Test
    public void addToAndSubtractFromBalance() {
        AccountState accstate = new AccountState();

        accstate.addToBalance(BigInteger.TEN);
        accstate.subtractFromBalance(BigInteger.ONE);
        Assert.assertEquals(9, accstate.getBalance().intValue());
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
}

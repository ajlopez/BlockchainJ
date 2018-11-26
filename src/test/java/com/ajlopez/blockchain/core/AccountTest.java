package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountTest {
    @Test
    public void createWithZeroBalanceAndZeroNonce() {
        Account accstate = new Account();

        Assert.assertEquals(BigInteger.ZERO, accstate.getBalance());
        Assert.assertEquals(0, accstate.getNonce());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonce() {
        Account accstate = new Account(null, 42);

        Assert.assertEquals(BigInteger.ZERO, accstate.getBalance());
        Assert.assertEquals(42, accstate.getNonce());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeBalance() {
        new Account(BigInteger.TEN.negate(), 0);
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new Account(BigInteger.TEN, -1);
    }
}

package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountStateTest {
    @Test
    public void createWithZeroBalance() {
        AccountState accstate = new AccountState();

        Assert.assertEquals(BigInteger.ZERO, accstate.getBalance());
    }
}

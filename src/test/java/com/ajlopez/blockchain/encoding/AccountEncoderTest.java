package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoderTest {
    @Test
    public void encodeDecodeAccountStateWithZeroBalanceAndZeroNonce() {
        Account state = new Account();

        byte[] encoded = AccountEncoder.encode(state);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountStateWithNonZeroBalance() {
        Account account = new Account(BigInteger.TEN, 0, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountStateWithNonZeroNonce() {
        Account account = new Account(BigInteger.ZERO, 42, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }
}

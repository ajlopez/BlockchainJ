package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.AccountState;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountStateEncoderTest {
    @Test
    public void encodeDecodeAccountStateWithZeroBalanceAndZeroNonce() {
        AccountState state = new AccountState();

        byte[] encoded = AccountStateEncoder.encode(state);

        Assert.assertNotNull(encoded);

        AccountState result = AccountStateEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountStateWithNonZeroBalance() {
        AccountState state = new AccountState();

        state.addToBalance(BigInteger.TEN);

        byte[] encoded = AccountStateEncoder.encode(state);

        Assert.assertNotNull(encoded);

        AccountState result = AccountStateEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.TEN, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountStateWithNonZeroNonce() {
        AccountState state = new AccountState(BigInteger.ZERO, 42);

        byte[] encoded = AccountStateEncoder.encode(state);

        Assert.assertNotNull(encoded);

        AccountState result = AccountStateEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }
}

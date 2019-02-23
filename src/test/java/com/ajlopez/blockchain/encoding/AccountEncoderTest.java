package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoderTest {
    @Test
    public void encodeDecodeAccountWithZeroBalanceAndZeroNonce() {
        Account account = new Account();

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
        Assert.assertNull(result.getCodeHash());
    }

    @Test
    public void encodeDecodeAccountWithCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        Account account = new Account(null, 0, codeHash);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
        Assert.assertEquals(codeHash, result.getCodeHash());
    }

    @Test
    public void encodeDecodeAccountWithNonZeroBalance() {
        Account account = new Account(BigInteger.valueOf(255), 0, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.valueOf(255), result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountWithNonZeroNonce() {
        Account account = new Account(BigInteger.ZERO, 42, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.ZERO, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
    }
}

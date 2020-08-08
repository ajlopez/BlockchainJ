package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by ajlopez on 19/11/2017.
 */
public class AccountEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeAccountWithZeroBalanceAndZeroNonce() {
        Account account = new Account();

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
        Assert.assertEquals(0, result.getCodeLength());
        Assert.assertNull(result.getCodeHash());
        Assert.assertNull(result.getStorageHash());
    }

    @Test
    public void encodeDecodeAccountWithCodeHash() {
        byte[] code = FactoryHelper.createRandomBytes(100);
        Hash codeHash = HashUtils.calculateHash(code);
        Account account = new Account(null, 0, code.length, codeHash, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
        Assert.assertEquals(code.length, result.getCodeLength());
        Assert.assertEquals(codeHash, result.getCodeHash());
        Assert.assertNull(result.getStorageHash());
    }

    @Test
    public void encodeDecodeAccountWithStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();
        Account account = new Account(null, 0, 0, null, storageHash);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
        Assert.assertEquals(0, result.getCodeLength());
        Assert.assertNull(result.getCodeHash());
        Assert.assertEquals(storageHash, result.getStorageHash());
    }

    @Test
    public void encodeDecodeAccountWithNonZeroBalance() {
        Account account = new Account(Coin.fromUnsignedLong(255), 0, 0, null, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.fromUnsignedLong(255), result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void encodeDecodeAccountWithNonZeroNonce() {
        Account account = new Account(Coin.ZERO, 42, 0, null, null);

        byte[] encoded = AccountEncoder.encode(account);

        Assert.assertNotNull(encoded);

        Account result = AccountEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(42, result.getNonce());
        Assert.assertEquals(0, result.getCodeLength());
        Assert.assertNull(result.getCodeHash());
        Assert.assertNull(result.getStorageHash());
    }

    @Test
    public void decodeInvalidEncodedAccount() {
        byte[] bytes1 = FactoryHelper.createRandomBytes(42);
        byte[] bytes2 = FactoryHelper.createRandomBytes(42);
        byte[] bytes3 = FactoryHelper.createRandomBytes(42);
        byte[] bytes4 = FactoryHelper.createRandomBytes(42);
        byte[] bytes5 = FactoryHelper.createRandomBytes(42);
        byte[] bytes6 = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(RLP.encode(bytes1), RLP.encode(bytes2), RLP.encode(bytes3), RLP.encode(bytes4), RLP.encode(bytes5), RLP.encode(bytes6));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid account encoding");
        AccountEncoder.decode(encoded);
    }
}

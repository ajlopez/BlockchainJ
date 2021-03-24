package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 24/03/2021.
 */
public class WalletTest {
    @Test
    public void emptyWallet() {
        Wallet wallet = new Wallet();

        List<Address> addresses = wallet.getAddresses();

        Assert.assertNotNull(addresses);
        Assert.assertTrue(addresses.isEmpty());
    }

    @Test
    public void addAddress() {
        Wallet wallet = new Wallet();
        Address address = FactoryHelper.createRandomAddress();

        wallet.addAddress(address);

        List<Address> addresses = wallet.getAddresses();

        Assert.assertNotNull(addresses);
        Assert.assertFalse(addresses.isEmpty());
        Assert.assertEquals(1, addresses.size());
        Assert.assertTrue(addresses.contains(address));
    }

    @Test
    public void addTwoAddresses() {
        Wallet wallet = new Wallet();
        Address address1 = FactoryHelper.createRandomAddress();
        Address address2 = FactoryHelper.createRandomAddress();

        wallet.addAddress(address1);
        wallet.addAddress(address2);

        List<Address> addresses = wallet.getAddresses();

        Assert.assertNotNull(addresses);
        Assert.assertFalse(addresses.isEmpty());
        Assert.assertEquals(2, addresses.size());
        Assert.assertTrue(addresses.contains(address1));
        Assert.assertTrue(addresses.contains(address2));
    }
}

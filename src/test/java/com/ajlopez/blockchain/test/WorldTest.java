package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class WorldTest {
    @Test
    public void getUnknownAccount() {
        World world = new World();

        Assert.assertNull(world.getAccount("foo"));
    }

    @Test
    public void setAndGetAccount() {
        World world = new World();
        Account account = new Account();

        world.setAccount("acc1", account);

        Account result = world.getAccount("acc1");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }
}

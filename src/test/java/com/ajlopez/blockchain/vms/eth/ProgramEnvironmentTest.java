package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironmentTest {
    @Test
    public void createEnvironmentWithAddresses() {
        Address address = FactoryHelper.createRandomAddress();
        Address origin = FactoryHelper.createRandomAddress();
        Address caller = FactoryHelper.createRandomAddress();

        ProgramEnvironment environment = new ProgramEnvironment(address, origin, caller);

        Assert.assertEquals(address, environment.getAddress());
        Assert.assertEquals(origin, environment.getOrigin());
        Assert.assertEquals(caller, environment.getCaller());
    }
}

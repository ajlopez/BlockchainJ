package com.ajlopez.blockchain.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 30/12/2018.
 */
public class NetworkConfigurationTest {
    @Test
    public void simpleCreationTest() {
        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)42);

        Assert.assertEquals(42, networkConfiguration.getNetworkNumber());
    }
}

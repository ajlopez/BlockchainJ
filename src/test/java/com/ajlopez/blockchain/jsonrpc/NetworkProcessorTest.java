package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 2019/04/08.
 */
public class NetworkProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void unknownMethod() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "net_foo", params);

        NetworkProcessor processor = new NetworkProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'net_foo'");
        processor.processRequest(request);
    }

    @Test
    public void networkIdUsingNetworkConfiguration() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "net_version", params);

        NetworkProcessor processor = new NetworkProcessor(new NetworkConfiguration(42));

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getVersion(), response.getVersion());
        Assert.assertEquals("\"0x2a\"", response.getResult().toString());
    }
}

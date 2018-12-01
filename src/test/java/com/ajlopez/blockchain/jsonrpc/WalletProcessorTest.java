package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class WalletProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void returnsEmptyArray() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_accounts", params);

        WalletProcessor processor = new WalletProcessor();

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getVersion(), response.getVersion());

        JsonValue result = response.getResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());
        Assert.assertEquals(0, ((JsonArrayValue)result).size());
    }

    @Test
    public void unknownMethod() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        WalletProcessor processor = new WalletProcessor();

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void invalidNumberOfParameters() throws JsonRpcException {
        List<JsonValue> params = Collections.singletonList(JsonConverter.convert("foo"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_accounts", params);

        WalletProcessor processor = new WalletProcessor();

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 0 found 1");
        processor.processRequest(request);
    }
}

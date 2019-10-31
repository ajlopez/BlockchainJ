package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
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
    public void getNoAccount() throws JsonRpcException, IOException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_accounts", params);

        WalletProcessor processor = new WalletProcessor(Collections.emptyList());

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());

        JsonValue result = response.getResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());
        Assert.assertEquals(0, ((JsonArrayValue)result).size());
    }

    @Test
    public void getAccounts() throws JsonRpcException, IOException {
        List<Address> addresses = FactoryHelper.createRandomAddresses(10);

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_accounts", params);

        WalletProcessor processor = new WalletProcessor(addresses);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());

        JsonValue result = response.getResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(JsonValueType.ARRAY, result.getType());

        JsonArrayValue aresult = (JsonArrayValue)result;

        Assert.assertEquals(10, aresult.size());

        int k = 0;

        for (Address address : addresses)
            Assert.assertEquals(address.toString(), aresult.getValue(k++).getValue());
    }

    @Test
    public void unknownMethod() throws JsonRpcException, IOException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        WalletProcessor processor = new WalletProcessor(Collections.emptyList());

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void invalidNumberOfParameters() throws JsonRpcException, IOException {
        List<JsonValue> params = Collections.singletonList(JsonConverter.convert("foo"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_accounts", params);

        WalletProcessor processor = new WalletProcessor(Collections.emptyList());

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 0 found 1");
        processor.processRequest(request);
    }
}

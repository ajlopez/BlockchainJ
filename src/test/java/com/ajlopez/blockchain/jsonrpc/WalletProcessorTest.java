package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonArrayValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class WalletProcessorTest {
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
}

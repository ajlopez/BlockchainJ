package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonNumericValue;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class AccountsProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void unknownMethod() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        AccountsProcessor processor = new AccountsProcessor(null, null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void getBalanceWithNoParameter() throws JsonRpcException {
        List<JsonValue> params = Collections.emptyList();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        AccountsProcessor processor = new AccountsProcessor(null, null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 0");
        processor.processRequest(request);
    }

    @Test
    public void getBalanceWithThreeParameters() throws JsonRpcException {
        List<JsonValue> params = new ArrayList();
        params.add(new JsonStringValue("foo"));
        params.add(new JsonStringValue("bar"));
        params.add(new JsonStringValue("foobar"));

        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        AccountsProcessor processor = new AccountsProcessor(null, null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 3");
        processor.processRequest(request);
    }
}

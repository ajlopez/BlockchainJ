package com.ajlopez.blockchain.jsonrpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajloprez on 02/12/2018.
 */
public class TopProcessor extends AbstractJsonRpcProcessor {
    private final Map<String, JsonRpcProcessor> processors = new HashMap<>();

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        String method = request.getMethod();

        if (this.processors.containsKey(method))
            return this.processors.get(method).processRequest(request);

        return super.processRequest(request);
    }

    public void registerProcess(String name, JsonRpcProcessor processor) {
        this.processors.put(name, processor);
    }
}

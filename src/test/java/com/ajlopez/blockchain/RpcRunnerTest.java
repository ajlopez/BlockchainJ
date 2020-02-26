package com.ajlopez.blockchain;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ajlopez on 26/02/2020.
 */
public class RpcRunnerTest {
    @Test
    public void simpleRequest() throws IOException {
        RpcRunner rpcRunner = new RpcRunner(6000, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("POST /\r\n");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        rpcRunner.stop();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);
    }
}

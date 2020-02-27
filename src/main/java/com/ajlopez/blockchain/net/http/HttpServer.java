package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.json.JsonLexerException;
import com.ajlopez.blockchain.json.JsonParserException;
import com.ajlopez.blockchain.jsonrpc.JsonRpcException;
import com.ajlopez.blockchain.jsonrpc.JsonRpcProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ajlopez on 14/04/2019.
 */
public class HttpServer {
    private final int port;
    private final JsonRpcProcessor jsonRpcProcessor;

    private boolean started;
    private boolean stopped;

    public HttpServer(int port, JsonRpcProcessor jsonRpcProcessor) {
        this.port = port;
        this.jsonRpcProcessor = jsonRpcProcessor;
    }

    public synchronized void start() {
        if (started)
            return;

        new Thread(this::process).start();

        this.started = true;
    }

    public void stop() {
        this.stopped = true;
    }

    private void process() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);

            while (!this.stopped) {
                Socket clientSocket = serverSocket.accept();

                // TODO review implementation
                byte[] data = new byte[1024 * 10];
                int ndata = clientSocket.getInputStream().read(data);
                InputStream inputStream = new ByteArrayInputStream(data, 0, ndata);
                Reader reader = new BufferedReader(new InputStreamReader(inputStream));

                OutputStream outputStream = clientSocket.getOutputStream();
                Writer writer = new PrintWriter(outputStream);
                HttpProcessor processor = new HttpProcessor(this.jsonRpcProcessor, reader, writer);

                processor.process();

                outputStream.flush();
                outputStream.close();

                clientSocket.close();
            }
        }
        catch (IOException | JsonLexerException | JsonParserException | JsonRpcException ex) {
            // TODO process exception
        }
    }
}

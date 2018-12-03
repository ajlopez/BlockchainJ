package com.ajlopez.blockchain.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

/**
 * Created by ajlopez on 03/12/2018.
 */
public class HttpRequestParser {
    public HttpRequest parse(Reader reader) throws IOException {
        BufferedReader breader = new BufferedReader(reader);

        String line = breader.readLine();

        StringTokenizer tokenizer = new StringTokenizer(line);

        String method = tokenizer.nextToken().toUpperCase();
        String resource = tokenizer.nextToken();

        while ((line = breader.readLine()) != null)
            if (line.trim().isEmpty())
                break;

        return new HttpRequest(method, resource, breader);
    }
}

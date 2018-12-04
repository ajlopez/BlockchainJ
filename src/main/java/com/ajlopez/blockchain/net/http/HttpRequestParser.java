package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.core.types.Hash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
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

        Map<String, String> headers = new HashMap<>();

        while ((line = breader.readLine()) != null)
            if (line.trim().isEmpty())
                break;
            else if (line.indexOf(": ") >= 0) {
                int p = line.indexOf(": ");
                String key = line.substring(0, p).trim();
                String value = line.substring(p + 2).trim();
                headers.put(key, value);
            }

        return new HttpRequest(method, resource, headers, breader);
    }
}

package com.ajlopez.blockchain.test.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ajlopez on 11/05/2019.
 */
public class DslParser {
    public DslCommand parse(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        if (line == null)
            return null;

        int p = line.indexOf('#');

        if (p >= 0)
            line = line.substring(0, p - 1);

        StringTokenizer tokenizer = new StringTokenizer(line);
        String verb = tokenizer.nextToken();

        List<String> arguments = new ArrayList<>();

        while (tokenizer.hasMoreTokens())
            arguments.add(tokenizer.nextToken());

        return new DslCommand(verb, arguments);
    }
}

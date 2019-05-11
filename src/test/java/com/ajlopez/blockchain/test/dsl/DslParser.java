package com.ajlopez.blockchain.test.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ajlopez on 11/05/2019.
 */
public class DslParser {
    public static DslCommand parse(String line) {
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

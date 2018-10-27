package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class Lexer {
    private Reader reader;

    public Lexer(Reader reader) {
        this.reader = reader;
    }

    public Token nextToken() throws IOException {
        Character ch = this.skipWhitespaces();

        if (ch == null)
            return null;

        StringBuffer buffer = new StringBuffer();
        buffer.append(ch);

        while ((ch = this.nextCharacter()) != null && !Character.isWhitespace(ch))
            buffer.append(ch);

        return new Token(TokenType.NAME, buffer.toString());
    }

    private Character skipWhitespaces() throws IOException {
        Character ch = this.nextCharacter();

        while (ch != null && Character.isWhitespace(ch))
            ch = this.nextCharacter();

        return ch;
    }

    private Character nextCharacter() throws IOException {
        int ch = reader.read();

        if (ch == -1)
            return null;

        return (char)ch;
    }
}

package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class Lexer {
    private Reader reader;
    private Stack<Character> chars = new Stack<>();

    public Lexer(Reader reader) {
        this.reader = reader;
    }

    public Token nextToken() throws IOException {
        Character ch = this.skipWhitespaces();

        if (ch == null)
            return null;

        StringBuffer buffer = new StringBuffer();
        buffer.append(ch);

        if (Character.isDigit(ch))
            return this.nextNumber(buffer);

        if (isInitialNameCharacter(ch))
            return this.nextName(buffer);

        return new Token(TokenType.SYMBOL, buffer.toString());
    }

    private static boolean isInitialNameCharacter(Character ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private static boolean isNameCharacter(Character ch) {
        return Character.isLetterOrDigit(ch)  || ch == '_';
    }

    private Token nextName(StringBuffer buffer) throws IOException {
        Character ch;

        while ((ch = this.nextCharacter()) != null && isNameCharacter(ch))
            buffer.append(ch);

        this.pushCharacter(ch);

        return new Token(TokenType.NAME, buffer.toString());
    }

    private Token nextNumber(StringBuffer buffer) throws IOException {
        Character ch;

        while ((ch = this.nextCharacter()) != null && Character.isDigit(ch))
            buffer.append(ch);

        return new Token(TokenType.NUMBER, buffer.toString());
    }

    private Character skipWhitespaces() throws IOException {
        Character ch = this.nextCharacter();

        while (ch != null && Character.isWhitespace(ch))
            ch = this.nextCharacter();

        return ch;
    }

    private void pushCharacter(Character ch) {
        if (ch != null)
            this.chars.push(ch);
    }

    private Character nextCharacter() throws IOException {
        if (!this.chars.empty())
            return this.chars.pop();

        int ch = reader.read();

        if (ch == -1)
            return null;

        return (char)ch;
    }
}

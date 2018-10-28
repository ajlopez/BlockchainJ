package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class Lexer {
    private Reader reader;
    private Stack<Token> tokens = new Stack<>();
    private Stack<Character> chars = new Stack<>();

    public Lexer(Reader reader) {
        this.reader = reader;
    }

    public void pushToken(Token token) {
        this.tokens.push(token);
    }

    public Token nextToken() throws IOException, LexerException {
        if (!this.tokens.isEmpty())
            return this.tokens.pop();

        Character ch = this.skipWhitespaces();

        if (ch == null)
            return null;

        if (ch == '"')
            return this.nextString();

        StringBuffer buffer = new StringBuffer();
        buffer.append(ch);

        if (Character.isDigit(ch))
            return this.nextNumber(buffer);

        if (isInitialNameCharacter(ch))
            return this.nextName(buffer);

        return new Token(TokenType.SYMBOL, buffer.toString());
    }

    private Token nextString() throws IOException, LexerException {
        StringBuffer buffer = new StringBuffer();

        Character ch;

        while ((ch = this.nextCharacter()) != null && ch != '"') {
            if (ch == '\\') {
                ch = this.nextCharacter();

                if (ch == null)
                    break;

                if (ch == 'n')
                    buffer.append('\n');
                else if (ch == 'r')
                    buffer.append('\r');
                else if (ch == 't')
                   buffer.append('\t');
                else if (ch == 't')
                    buffer.append('\t');
                else if (ch == '\\')
                    buffer.append('\\');
                else if (ch == '"')
                    buffer.append('"');
                else
                    buffer.append(ch);
            }
            else
                buffer.append(ch);
        }

        if (ch == null)
            throw new LexerException("Unclosed string");

        return new Token(TokenType.STRING, buffer.toString());
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

        this.pushCharacter(ch);

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

    private static boolean isInitialNameCharacter(Character ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private static boolean isNameCharacter(Character ch) {
        return Character.isLetterOrDigit(ch)  || ch == '_';
    }
}

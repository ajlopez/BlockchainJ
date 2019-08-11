package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 11/08/2019.
 */
public class Chunker {
    private final int chunkLength;
    private final InputStream inputStream;

    public Chunker(int chunkLength, InputStream inputStream) {
        this.chunkLength = chunkLength;
        this.inputStream = inputStream;
    }

    public byte[] nextChunk() throws IOException {
        byte[] chunk = new byte[this.chunkLength];

        int nbytes = this.inputStream.read(chunk);

        if (nbytes == -1)
            return null;

        if (nbytes < this.chunkLength)
            return ByteUtils.copyBytes(chunk, nbytes);

        return chunk;
    }
}

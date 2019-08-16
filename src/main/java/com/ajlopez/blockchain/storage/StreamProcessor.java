package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 14/08/2019.
 */
public class StreamProcessor {
    private final ChunkStore chunkStore;

    public StreamProcessor(ChunkStore chunkStore) {
        this.chunkStore = chunkStore;
    }

    public void processStream(InputStream stream, int chunkLength) throws IOException {
        Chunker chunker = new Chunker(chunkLength, stream);
        ByteArrayOutputStream hashStream = new ByteArrayOutputStream();

        for (Chunk chunk = chunker.nextChunk(); chunk != null; chunk = chunker.nextChunk()) {
            this.chunkStore.saveChunk(chunk);
            hashStream.write(chunk.getHash().getBytes());
        }

        hashStream.close();

        byte[] hashBytes = hashStream.toByteArray();

        if (hashBytes.length <= Hash.HASH_BYTES)
            return;

        InputStream hashes = new ByteArrayInputStream(hashBytes);

        this.processStream(hashes, chunkLength);
    }
}

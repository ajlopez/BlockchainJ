package com.ajlopez.blockchain.storage;

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

        for (Chunk chunk = chunker.nextChunk(); chunk != null; chunk = chunker.nextChunk())
            this.chunkStore.saveChunk(chunk);
    }
}

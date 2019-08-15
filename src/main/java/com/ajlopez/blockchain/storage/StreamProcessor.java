package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

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
        ByteOutputStream hashStream = new ByteOutputStream();

        for (Chunk chunk = chunker.nextChunk(); chunk != null; chunk = chunker.nextChunk()) {
            this.chunkStore.saveChunk(chunk);
            hashStream.write(chunk.getHash().getBytes());
        }

        hashStream.close();

        byte[] hashBytes = hashStream.getBytes();
        int nHashBytes = hashStream.getCount();

        if (nHashBytes <= Hash.HASH_BYTES)
            return;

        InputStream hashes = new ByteInputStream(hashBytes, nHashBytes);

        this.processStream(hashes, chunkLength);
    }
}

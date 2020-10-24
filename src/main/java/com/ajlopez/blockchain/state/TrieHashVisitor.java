package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieStore;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by ajlopez on 24/10/2020.
 */
public abstract class TrieHashVisitor {
    private final TrieStore trieStore;
    private final Queue<Hash> queue = new ArrayDeque<>();

    public TrieHashVisitor(TrieStore trieStore) {
        this.trieStore = trieStore;
    }

    public void process(Hash hash) throws IOException {
        this.toProcess(hash);
        this.process();
    }

    public void process() throws IOException {
        while (!this.queue.isEmpty()) {
            Hash hash = this.queue.poll();
            Trie node = this.trieStore.retrieve(hash);

            processNode(node);

            for (Hash subhash : node.getSubHashes()) {
                if (subhash != null)
                    this.toProcess(subhash);
            }
        }
    }

    public void toProcess(Hash hash) {
        this.queue.add(hash);
    }

    public abstract void processNode(Trie trie) throws IOException;
}

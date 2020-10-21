package com.ajlopez.blockchain.state;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by ajlopez on 21/10/2020.
 */
public abstract class TrieNodeVisitor {
    private final Queue<Trie> queue = new ArrayDeque<>();

    public void process(Trie trie) throws IOException {
        this.toProcess(trie);
        this.process();
    }

    public void process() throws IOException {
        while (!this.queue.isEmpty()) {
            Trie node = this.queue.poll();

            processNode(node);

            for (int k = 0; k < Trie.ARITY; k++) {
                Trie subnode = node.getSubNode(k);

                if (subnode != null)
                    this.toProcess(subnode);
            }
        }
    }

    public void toProcess(Trie trie) {
        this.queue.add(trie);
    }

    public abstract void processNode(Trie trie);
}

package com.ajlopez.blockchain.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 05/01/2020.
 */
public class TriePath {
    private final List<Trie> tries = new ArrayList<>();
    private final List<Integer> positions = new ArrayList<>();

    public TriePath() {

    }

    public int getSize() { return this.tries.size(); }

    public Trie getTrie(int n) { return this.tries.get(n); }

    public int getChildPosition(int n) {
        return this.positions.get(n);
    }

    public void addLastTrie(Trie trie) {
        // TODO close the path
        this.tries.add(trie);
    }

    public void addTrieAndChildPosition(Trie trie, int position) {
        this.tries.add(trie);
        this.positions.add(position);
    }
}

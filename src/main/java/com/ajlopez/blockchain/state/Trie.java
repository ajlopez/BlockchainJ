package com.ajlopez.blockchain.state;

import java.util.Arrays;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Trie {
    private static final int ARITY = 16;

    private static Trie empty = new Trie();
    private byte[] value;
    private Trie[] nodes;

    private Trie() {}

    public static Trie getEmptyTrie() { return empty; }

    private Trie(Trie[] nodes, byte[] value) {
        this.nodes = nodes;
        this.value = value;
    }

    public int nodesSize() {
        int count = 1;

        if (this.nodes != null)
            for (Trie node : this.nodes)
                if (node != null)
                    count += node.nodesSize();

        return count;
    }

    public byte[] get(byte[] key) {
        return this.get(key, getInitialPosition(key));
    }

    private byte[] get(byte[] key, int position) {
        if (position == key.length * 2)
            return this.value;

        if (this.nodes == null)
            return null;

        int nibble = getOffset(key, position);

        if (this.nodes[nibble] == null)
            return null;

        return this.nodes[nibble].get(key, position + 1);
    }

    public Trie put(byte[] key, byte[] value) {
        Trie trie = this.put(key, getInitialPosition(key), value);

        if (trie == null)
            return empty;

        return trie;
    }

    public Trie delete(byte[] key) {
        return this.put(key, null);
    }

    private Trie put(byte[] key, int position, byte[] value) {
        if (position == key.length * 2)
            if (Arrays.equals(value, this.value))
                return this;
            else
                return createNewTrie(this.nodes, value, true);

        int offset = getOffset(key, position);

        Trie[] children = copyNodes(this.nodes, true);

        if (children[offset] == null)
            children[offset] = empty.put(key, position + 1, value);
        else
            children[offset] = children[offset].put(key, position + 1, value);

        if (noNode(children))
            children = null;

        return createNewTrie(children, this.value, false);
    }

    private static Trie createNewTrie(Trie[] nodes, byte[] value, boolean copy) {
        if (value == null && noNode(nodes))
            return null;

        if (copy)
            return new Trie(copyNodes(nodes, false), value);

        return new Trie(nodes, value);
    }

    private static boolean noNode(Trie[] nodes) {
        if (nodes == null)
            return true;

        for (int k = 0; k < nodes.length; k++)
            if (nodes[k] != null)
                return false;

        return true;
    }

    private static Trie[] copyNodes(Trie[] nodes, boolean create) {
        if (nodes == null)
            return create ? new Trie[ARITY] : null;

        return Arrays.copyOf(nodes, ARITY);
    }

    private static int getOffset(byte[] key, int position) {
        if (position % 2 == 0)
            return (key[position / 2] >> 4) & 0x0f;

        return key[position / 2] & 0x0f;
    }

    private static int getInitialPosition(byte[] key) {
        int l = key.length;
        int k = 0;

        for (; k < l && key[k] == 0; k++)
            ;

        if (k < l && (key[k] & 0xf0) == 0)
            k = k * 2 + 1;
        else
            k = k * 2;

        return k;
    }
}

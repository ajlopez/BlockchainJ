package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.Arrays;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Trie {
    private static final int ARITY = 16;

    private static Trie empty = new Trie();

    private byte[] value;
    private Trie[] nodes;

    private Hash hash;

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
        return this.get(key, 0);
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
        Trie trie = this.put(key, 0, value);

        if (trie == null)
            return empty;

        return trie;
    }

    public Trie delete(byte[] key) {
        return this.put(key, null);
    }

    public Hash getHash() {
        if (this.hash != null)
            return this.hash;

        this.hash = HashUtils.calculateHash(this.getEncoded());

        return this.hash;
    }

    public byte[] getEncoded() {
        int valsizebytes = 0;
        int valbytes = 0;

        if (this.value != null && this.value.length > 0) {
            valsizebytes = Integer.BYTES;
            valbytes = this.value.length;
        }

        int nsubnodes = this.getSubnodesCount();

        byte[] bytes = new byte[1 + 1 + 1 + 2 + HashUtils.HASH_BYTES * nsubnodes + valsizebytes + valbytes];

        short subnodes = 0;
        int nsubnode = 0;

        if (this.nodes != null)
            for (int k = 0; k < this.nodes.length; k++) {
                if (this.nodes[k] == null)
                    continue;

                subnodes |= 1 << k;
                Hash subhash = this.nodes[k].getHash();
                System.arraycopy(subhash.getBytes(), 0, bytes, 1 + 1 + 1 + 2 + HashUtils.HASH_BYTES * nsubnode, HashUtils.HASH_BYTES);
                nsubnode++;
            }

        byte[] subnodesbits = ByteUtils.unsignedShortToBytes(subnodes);

        // byte[0] version == 0

        // arity
        bytes[1] = 16;

        // byte[2] flags

        // byte[3..4] subnode bits
        bytes[3] = subnodesbits[0];
        bytes[4] = subnodesbits[1];

        // value size

        if (valsizebytes > 0) {
            System.arraycopy(ByteUtils.unsignedIntegerToBytes(valbytes), 0, bytes, 5, valsizebytes);
            System.arraycopy(this.value, 0, bytes, 5 + valsizebytes, valbytes);
        }

        // subnodes hashes

        return bytes;
    }

    private int getSubnodesCount() {
        if (this.nodes == null)
            return 0;

        int nsubnodes = 0;

        for (int k = 0; k < this.nodes.length; k++)
            if (this.nodes[k] != null)
                nsubnodes++;

        return nsubnodes;
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
}

package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieStore;
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
    private Hash[] hashes;
    private TrieStore store;

    private Hash hash;

    private Trie() {}

    public static Trie getEmptyTrie() { return empty; }

    private Trie(Trie[] nodes, byte[] value) {
        this.nodes = nodes;
        this.value = value;
    }

    private Trie(Hash[] hashes) {
        this.hashes = hashes;
    }

    private Trie(Hash[] hashes, byte[] value) {
        this.hashes = hashes;
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

        byte[] bytes = new byte[1 + 1 + 1 + Short.BYTES + HashUtils.HASH_BYTES * nsubnodes + valsizebytes + valbytes];

        getSubnodes(bytes, 1 + 1 + 1);

        // byte[0] version == 0

        // arity
        bytes[1] = ARITY;

        // value size

        bytes[2] = (byte)valsizebytes;

        // value encoding

        if (valsizebytes > 0) {
            System.arraycopy(ByteUtils.unsignedIntegerToBytes(valbytes), 0, bytes, 1 + 1 + 1 + Short.BYTES + HashUtils.HASH_BYTES * nsubnodes, valsizebytes);
            System.arraycopy(this.value, 0, bytes, 1 + 1 + 1 + Short.BYTES + HashUtils.HASH_BYTES * nsubnodes + valsizebytes, valbytes);
        }

        // subnodes hashes

        return bytes;
    }

    public static Trie fromEncoded(byte[] bytes, TrieStore store) {
        short valsizebytes = bytes[2];
        short subnodes = ByteUtils.bytesToUnsignedShort(bytes, 3);

        if (subnodes == 0 && valsizebytes == 0)
            return new Trie();

        Hash[] hashes = new Hash[ARITY];
        int h = 0;

        for (int k = 0; k < ARITY; k++) {
            if ((subnodes & (1 << k)) == 0)
                continue;

            byte[] bhash = new byte[HashUtils.HASH_BYTES];
            System.arraycopy(bytes, 3 + Short.BYTES + HashUtils.HASH_BYTES * h, bhash, 0, HashUtils.HASH_BYTES);
            hashes[k] = new Hash(bhash);

            h++;
        }

        if (valsizebytes == 0)
            return new Trie(hashes);

        int lvalue = ByteUtils.bytesToUnsignedInteger(bytes, 3 + Short.BYTES + HashUtils.HASH_BYTES * h);

        byte[] value = new byte[lvalue];
        System.arraycopy(bytes, 3 + Short.BYTES + HashUtils.HASH_BYTES * h + valsizebytes, value, 0, lvalue);

        return new Trie(hashes, value);
    }

    private void getSubnodes(byte[] bytes, int offset) {
        short subnodes = 0;
        int nsubnode = 0;

        if (this.nodes != null || this.hashes != null)
            for (int k = 0; k < ARITY; k++) {
                Hash subhash = this.getSubhash(k);

                if (subhash == null)
                    continue;

                subnodes |= 1 << k;
                System.arraycopy(subhash.getBytes(), 0, bytes, offset + Short.BYTES + HashUtils.HASH_BYTES * nsubnode, HashUtils.HASH_BYTES);
                nsubnode++;
            }

        byte[] subnodesbits = ByteUtils.unsignedShortToBytes(subnodes);

        System.arraycopy(subnodesbits, 0, bytes, offset, subnodesbits.length);
    }

    private Hash getSubhash(int k) {
        Hash hash = this.getSubhashFromHashes(k);

        if (hash != null)
            return hash;

        hash = this.getSubhashFromNodes(k);

        if (hash == null)
            return null;

        this.setSubhash(k, hash);

        return hash;
    }

    private Hash getSubhashFromHashes(int k) {
        if (this.hashes == null)
            return null;

        return this.hashes[k];
    }

    private void setSubhash(int k, Hash hash) {
        if (this.hashes == null)
            this.hashes = new Hash[ARITY];

        this.hashes[k] = hash;
    }

    private Hash getSubhashFromNodes(int k) {
        Trie node = this.getSubnode(k);

        if (node == null)
            return null;

        return node.getHash();
    }

    private Trie getSubnode(int k) {
        if (this.nodes == null)
            return null;

        return this.nodes[k];
    }

    private int getSubnodesCount() {
        if (this.nodes == null)
            return this.getSubhashesCount();

        int nsubnodes = 0;

        for (int k = 0; k < this.nodes.length; k++)
            if (this.nodes[k] != null || (this.hashes != null && this.hashes[k] != null))
                nsubnodes++;

        return nsubnodes;
    }

    private int getSubhashesCount() {
        if (this.hashes == null)
            return 0;

        int nsubnodes = 0;

        for (int k = 0; k < this.hashes.length; k++)
            if (this.hashes[k] != null)
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

        if (noNodes(children))
            children = null;

        return createNewTrie(children, this.value, false);
    }

    private static Trie createNewTrie(Trie[] nodes, byte[] value, boolean copy) {
        if (value == null && noNodes(nodes))
            return null;

        if (copy)
            return new Trie(copyNodes(nodes, false), value);

        return new Trie(nodes, value);
    }

    private static boolean noNodes(Trie[] nodes) {
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

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
    public static final Hash EMPTY_TRIE_HASH = new Trie().getHash();
    public static final int ARITY = 16;

    private byte[] value;
    private Trie[] nodes;
    private Hash[] hashes;
    private TrieStore store;

    private Hash hash;
    private byte[] encoded;
    private boolean saved;

    public Trie() {}

    public Trie(TrieStore store) {
        this(null, null, null, store);
    }

    private Trie(Trie[] nodes, Hash[] hashes, byte[] value, TrieStore store) {
        this.nodes = nodes;
        this.hashes = hashes;
        this.value = value;
        this.store = store;
    }

    public int nodesSize() {
        int count = 1;

        if (this.nodes != null)
            for (Trie node : this.nodes)
                if (node != null)
                    count += node.nodesSize();

        return count;
    }

    public Hash[] getSubHashes() {
        if (this.hashes == null)
            this.hashes = new Hash[Trie.ARITY];

        if (this.nodes != null)
            for (int k = 0; k < Trie.ARITY; k++)
                if (this.hashes[k] == null && this.nodes[k] != null)
                    this.hashes[k] = this.getSubHash(k);

        Hash[] result = new Hash[Trie.ARITY];

        System.arraycopy(this.hashes, 0, result, 0, Trie.ARITY);

        return result;
    }

    public byte[] get(byte[] key) {
        return this.get(key, 0);
    }

    private byte[] get(byte[] key, int position) {
        if (position == key.length * 2)
            return this.value;

        int nibble = getOffset(key, position);

        Trie trie = this.getSubNode(nibble);

        if (trie == null)
            return null;

        return trie.get(key, position + 1);
    }

    public Trie put(byte[] key, byte[] value) {
        Trie trie = this.put(key, 0, value);

        if (trie == null)
            return new Trie(this.store);

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
        if (this.encoded != null)
            return this.encoded;

        int valsizebytes = 0;
        int valbytes = 0;

        if (this.value != null && this.value.length > 0) {
            valsizebytes = Integer.BYTES;
            valbytes = this.value.length;
        }

        int nsubnodes = this.getSubNodesCount();

        byte[] bytes = new byte[1 + 1 + 1 + Short.BYTES + HashUtils.HASH_BYTES * nsubnodes + valsizebytes + valbytes];

        getSubNodes(bytes, 1 + 1 + 1);

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

        this.encoded = bytes;

        return this.encoded;
    }

    public void save() {
        if (this.saved)
            return;

        // TODO review
        if (this.store != null)
            this.store.save(this);

        this.saved = true;

        if (this.nodes == null)
            return;

        for (int k = 0; k < ARITY; k++) {
            Trie node = this.nodes[k];

            if (node != null)
                node.save();
        }
    }

    public static Trie fromEncoded(byte[] bytes, TrieStore store) {
        short valsizebytes = bytes[2];
        short subnodes = ByteUtils.bytesToUnsignedShort(bytes, 3);

        if (subnodes == 0 && valsizebytes == 0)
            return new Trie(store);

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
            return new Trie(null, hashes, null, store);

        int lvalue = ByteUtils.bytesToUnsignedInteger(bytes, 3 + Short.BYTES + HashUtils.HASH_BYTES * h);

        byte[] value = new byte[lvalue];
        System.arraycopy(bytes, 3 + Short.BYTES + HashUtils.HASH_BYTES * h + valsizebytes, value, 0, lvalue);

        return new Trie(null, hashes, value, store);
    }

    private void getSubNodes(byte[] bytes, int offset) {
        short subnodes = 0;
        int nsubnode = 0;

        if (this.nodes != null || this.hashes != null)
            for (int k = 0; k < ARITY; k++) {
                Hash subhash = this.getSubHash(k);

                if (subhash == null)
                    continue;

                subnodes |= 1 << k;
                System.arraycopy(subhash.getBytes(), 0, bytes, offset + Short.BYTES + HashUtils.HASH_BYTES * nsubnode, HashUtils.HASH_BYTES);
                nsubnode++;
            }

        byte[] subnodesbits = ByteUtils.unsignedShortToBytes(subnodes);

        System.arraycopy(subnodesbits, 0, bytes, offset, subnodesbits.length);
    }

    private Hash getSubHash(int k) {
        Hash hash = this.getSubHashFromHashes(k);

        if (hash != null)
            return hash;

        hash = this.getSubHashFromNodes(k);

        if (hash == null)
            return null;

        this.setSubHash(k, hash);

        return hash;
    }

    private Hash getSubHashFromHashes(int k) {
        if (this.hashes == null)
            return null;

        return this.hashes[k];
    }

    private void setSubHash(int k, Hash hash) {
        if (this.hashes == null)
            this.hashes = new Hash[ARITY];

        this.hashes[k] = hash;
    }

    private Hash getSubHashFromNodes(int k) {
        if (this.nodes == null)
            return null;

        Trie node = this.nodes[k];

        if (node == null)
            return null;

        return node.getHash();
    }

    private Trie getSubNode(int k) {
        if (this.nodes != null && this.nodes[k] != null)
            return this.nodes[k];

        if (this.hashes == null)
            return null;

        Hash hash = this.hashes[k];

        if (hash == null)
            return null;

        Trie trie = this.store.retrieve(hash);

        this.nodes = new Trie[ARITY];
        this.nodes[k] = trie;

        return trie;
    }

    private int getSubNodesCount() {
        boolean hasnodes = this.nodes != null;
        boolean hashashes = this.hashes != null;

        int nsubnodes = 0;

        for (int k = 0; k < ARITY; k++)
            if ((hasnodes && this.nodes[k] != null) || (hashashes && this.hashes[k] != null))
                nsubnodes++;

        return nsubnodes;
    }

    private Trie put(byte[] key, int position, byte[] value) {
        if (position == key.length * 2)
            if (Arrays.equals(value, this.value))
                return this;
            else
                return createNewTrie(this.nodes, this.hashes, value, true, this.store);

        int offset = getOffset(key, position);

        Trie[] childNodes = copyNodes(this.nodes, true);
        Hash[] childHashes = copyHashes(this.hashes, true);

        childHashes[offset] = null;

        if (childNodes[offset] == null)
            childNodes[offset] = new Trie(this.store).put(key, position + 1, value);
        else
            childNodes[offset] = childNodes[offset].put(key, position + 1, value);

        if (noNodes(childNodes))
            childNodes = null;

        return createNewTrie(childNodes, childHashes, this.value, false, this.store);
    }

    private static Trie createNewTrie(Trie[] nodes, Hash[] hashes, byte[] value, boolean copy, TrieStore store) {
        if (value == null && noNodes(nodes))
            return null;

        if (copy)
            return new Trie(copyNodes(nodes, false), hashes, value, store);

        return new Trie(nodes, hashes, value, store);
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

    private static Hash[] copyHashes(Hash[] hashes, boolean create) {
        if (hashes == null)
            return create ? new Hash[ARITY] : null;

        return Arrays.copyOf(hashes, ARITY);
    }

    private static int getOffset(byte[] key, int position) {
        if (position % 2 == 0)
            return (key[position / 2] >> 4) & 0x0f;

        return key[position / 2] & 0x0f;
    }
}

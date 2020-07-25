package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ajlopez on 19/07/2020.
 */
public class WorldStateCopier {
    private final TrieStore sourceAccountTrieStore;
    private final TrieStore targetAccountTrieStore;
    private final TrieStore sourceStorageTrieStore;
    private final TrieStore targetStorageTrieStore;
    private final CodeStore sourceCodeStore;
    private final CodeStore targetCodeStore;

    private final Queue<KeyInformation> hashes = new LinkedList<>();

    public WorldStateCopier(Stores sourceStores, Stores targetStores, Hash rootHash) {
        this.sourceAccountTrieStore = sourceStores.getAccountTrieStore();
        this.targetAccountTrieStore = targetStores.getAccountTrieStore();
        this.sourceStorageTrieStore = sourceStores.getStorageTrieStore();
        this.targetStorageTrieStore = targetStores.getStorageTrieStore();
        this.sourceCodeStore = sourceStores.getCodeStore();
        this.targetCodeStore = targetStores.getCodeStore();

        this.hashes.add(new KeyInformation(KeyValueStoreType.ACCOUNTS, rootHash));
    }

    public void process() throws IOException {
        while (!this.hashes.isEmpty()) {
            KeyInformation keyInformation = this.hashes.poll();

            if (keyInformation.getKeyValueStoreType() == KeyValueStoreType.ACCOUNTS)
                processAccountNodeHash(keyInformation.getHash());
            else if (keyInformation.getKeyValueStoreType() == KeyValueStoreType.STORAGE)
                processStorageNodeHash(keyInformation.getHash());
            else if (keyInformation.getKeyValueStoreType() == KeyValueStoreType.CODES)
                processCodeHash(keyInformation.getHash());
        }
    }

    private void processAccountNodeHash(Hash hash) throws IOException {
        Trie trie = this.sourceAccountTrieStore.retrieve(hash);

        if (!this.targetAccountTrieStore.exists(hash))
            this.targetAccountTrieStore.save(trie);

        Hash[] subhashes = trie.getSubHashes();

        for (int k = 0; k < subhashes.length; k++)
            if (subhashes[k] != null)
                this.hashes.add(new KeyInformation(KeyValueStoreType.ACCOUNTS, subhashes[k]));

        byte[] value = trie.getValue();

        if (value == null)
            return;

        Account account = AccountEncoder.decode(value);

        this.hashes.add(new KeyInformation(KeyValueStoreType.STORAGE, account.getStorageHash()));

        Hash codeHash = account.getCodeHash();

        if (codeHash != null)
            this.hashes.add(new KeyInformation(KeyValueStoreType.CODES, codeHash));
    }

    private void processStorageNodeHash(Hash hash) throws IOException {
        Trie trie = this.sourceStorageTrieStore.retrieve(hash);

        if (!this.targetStorageTrieStore.exists(hash))
            this.targetStorageTrieStore.save(trie);

        Hash[] subhashes = trie.getSubHashes();

        for (int k = 0; k < subhashes.length; k++)
            if (subhashes[k] != null)
                this.hashes.add(new KeyInformation(KeyValueStoreType.STORAGE, subhashes[k]));
    }

    private void processCodeHash(Hash hash) throws IOException {
        if (this.targetCodeStore.getCode(hash) != null)
            return;

        byte[] code = this.sourceCodeStore.getCode(hash);

        this.targetCodeStore.putCode(hash, code);
    }
}

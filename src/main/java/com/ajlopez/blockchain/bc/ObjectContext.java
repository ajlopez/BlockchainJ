package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.store.KeyValueStores;
import com.ajlopez.blockchain.store.Stores;

/**
 * Created by ajlopez on 29/03/2021.
 */
public class ObjectContext {
    private final KeyValueStores keyValueStores;

    private Stores stores;
    private BlockChain blockChain;
    private TransactionPool transactionPool;

    public ObjectContext(KeyValueStores keyValueStores) {
        this.keyValueStores = keyValueStores;
    }

    public Stores getStores() {
        if (this.stores != null)
            return this.stores;

        this.stores = new Stores(this.keyValueStores);

        return this.stores;
    }

    public BlockChain getBlockChain() {
        if (this.blockChain != null)
            return this.blockChain;

        this.blockChain = new BlockChain(this.getStores());

        return this.blockChain;
    }

    public TransactionPool getTransactionPool() {
        if (this.transactionPool != null)
            return this.transactionPool;

        this.transactionPool = new TransactionPool();

        return this.transactionPool;
    }

    public KeyValueStores getKeyValueStores() {
        return this.keyValueStores;
    }
}

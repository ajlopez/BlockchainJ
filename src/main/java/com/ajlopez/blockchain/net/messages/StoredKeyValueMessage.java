package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.store.KeyValueStoreType;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class StoredKeyValueMessage extends Message {
    private final KeyValueStoreType storeType;
    private final byte[] key;
    private final byte[] value;

    public StoredKeyValueMessage(KeyValueStoreType storeType, byte[] key, byte[] value) {
        super(MessageType.STORED_KEY_VALUE);
        this.storeType = storeType;
        this.key = key;
        this.value = value;
    }

    public KeyValueStoreType getStoreType() { return this.storeType; }

    public byte[] getKey() { return this.key; }

    public byte[] getValue() { return this.value; }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.storeType.ordinal() };
        return RLP.encodeList(RLP.encode(type), RLP.encode(this.key), RLP.encode(this.value));
    }
}

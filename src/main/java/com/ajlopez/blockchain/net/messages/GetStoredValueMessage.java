package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.store.StoreType;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class GetStoredValueMessage extends Message {
    private final StoreType storeType;
    private final byte[] key;

    public GetStoredValueMessage(StoreType storeType, byte[] key) {
        super(MessageType.GET_STORED_VALUE);
        this.storeType = storeType;
        this.key = key;
    }

    public StoreType getStoreType() { return this.storeType; }

    public byte[] getKey() { return this.key; }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.storeType.ordinal() };
        return RLP.encodeList(RLP.encode(type), RLP.encode(this.key));
    }
}

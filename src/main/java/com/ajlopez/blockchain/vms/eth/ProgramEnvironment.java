package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironment {
    private final MessageData messageData;
    private final BlockData blockData;

    public ProgramEnvironment(MessageData messageData, BlockData blockData) {
        this.messageData = messageData;
        this.blockData = blockData;
    }

    public Address getAddress() { return this.messageData.getAddress(); }

    public Address getOrigin() { return this.messageData.getOrigin(); }

    public Address getCaller() { return this.messageData.getCaller(); }

    public DataWord getValue() { return this.messageData.getValue(); }

    public long getNumber() { return this.blockData.getNumber(); }

    public DataWord getDifficulty() { return this.blockData.getDifficulty(); }

    public long getTimestamp() { return this.blockData.getTimestamp(); }

    public Address getCoinbase() { return this.blockData.getCoinbase(); }

    public byte[] getData() { return this.messageData.getData(); }

    public DataWord getGasPrice() { return this.messageData.getGasPrice(); }
}

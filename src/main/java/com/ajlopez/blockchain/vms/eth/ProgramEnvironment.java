package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.ExecutionContext;

import java.io.IOException;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironment {
    private final MessageData messageData;
    private final BlockData blockData;
    private final ExecutionContext executionContext;
    private final int chainId;

    public ProgramEnvironment(MessageData messageData, BlockData blockData, ExecutionContext executionContext, int chainId) {
        this.messageData = messageData;
        this.blockData = blockData;
        this.executionContext = executionContext;
        this.chainId = chainId;
    }

    public ProgramEnvironment createChildEnvironment(Address caller, Address callee, Coin newValue, long newGas, byte[] newData, int outputDataOffset, int outputDataSize) {
        MessageData newMessageData = new MessageData(
            callee,
            this.getOrigin(),
            caller,
            newValue,
            newGas,
            this.getGasPrice(),
            newData,
            outputDataOffset,
            outputDataSize,
            this.isReadOnly()
        );

        return new ProgramEnvironment(
            newMessageData,
            this.blockData,
            this.executionContext.createChildExecutionContext(),
            this.chainId
        );
    }

    public Address getAddress() { return this.messageData.getAddress(); }

    public Coin getBalance(Address address) throws IOException { return this.executionContext.getBalance(address); }

    public byte[] getCode(Address address) throws IOException { return this.executionContext.getCode(address); }

    public long getCodeLength(Address address)  throws IOException { return this.executionContext.getCodeLength(address); }

    public Hash getCodeHash(Address address)  throws IOException { return this.executionContext.getCodeHash(address); }

    public Address getOrigin() { return this.messageData.getOrigin(); }

    public Address getCaller() { return this.messageData.getCaller(); }

    public Coin getValue() { return this.messageData.getValue(); }

    public long getNumber() { return this.blockData.getNumber(); }

    public Difficulty getDifficulty() { return this.blockData.getDifficulty(); }

    public long getGasLimit() { return this.blockData.getGasLimit(); }

    public long getTimestamp() { return this.blockData.getTimestamp(); }

    public Address getCoinbase() { return this.blockData.getCoinbase(); }

    public byte[] getData() { return this.messageData.getData(); }

    public long getGas() { return this.messageData.getGas(); }

    public Coin getGasPrice() { return this.messageData.getGasPrice(); }

    public boolean isReadOnly() { return this.messageData.isReadOnly(); }

    public int getChainId() { return this.chainId; }

    public Storage getAccountStorage(Address address) throws IOException {
        return this.executionContext.getAccountStorage(address);
    }

    // TODO review design of this data
    public int getOutputDataOffset() {
        return this.messageData.getOutputDataOffset();
    }

    // TODO review design of this data
    public int getOutputDataSize() {
        return this.messageData.getOutputDataSize();
    }

    // TODO redesign/review
    public void commit() throws IOException {
        this.executionContext.commit();
    }

    // TODO redesign/review
    public void rollback() {
        this.executionContext.rollback();
    }
}

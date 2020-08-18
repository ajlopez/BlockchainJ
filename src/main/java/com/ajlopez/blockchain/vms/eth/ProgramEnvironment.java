package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.CodeProvider;

import java.io.IOException;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironment {
    private final MessageData messageData;
    private final BlockData blockData;
    private final CodeProvider codeProvider;

    public ProgramEnvironment(MessageData messageData, BlockData blockData, CodeProvider codeProvider) {
        this.messageData = messageData;
        this.blockData = blockData;
        this.codeProvider = codeProvider;
    }

    public Address getAddress() { return this.messageData.getAddress(); }

    public byte[] getCode(Address address) throws IOException { return this.codeProvider.getCode(address); }

    public long getCodeLength(Address address)  throws IOException { return this.codeProvider.getCodeLength(address); }

    public Hash getCodeHash(Address address)  throws IOException { return this.codeProvider.getCodeHash(address); }

    public Address getOrigin() { return this.messageData.getOrigin(); }

    public Address getCaller() { return this.messageData.getCaller(); }

    public Coin getValue() { return this.messageData.getValue(); }

    public long getNumber() { return this.blockData.getNumber(); }

    public Difficulty getDifficulty() { return this.blockData.getDifficulty(); }

    public long getTimestamp() { return this.blockData.getTimestamp(); }

    public Address getCoinbase() { return this.blockData.getCoinbase(); }

    public byte[] getData() { return this.messageData.getData(); }

    public long getGas() { return this.messageData.getGas(); }

    public Coin getGasPrice() { return this.messageData.getGasPrice(); }

    public boolean isReadOnly() { return this.messageData.isReadOnly(); }
}

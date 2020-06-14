package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.TransactionReceiptEncoder;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 25/05/2020.
 */
public class TransactionReceipt {
    private final long gasUsed;
    private final boolean success;
    private final List<Log> logs;

    private Hash hash;

    public TransactionReceipt(long gasUsed, boolean success, List<Log> logs) {
        this.gasUsed = gasUsed;
        this.success = success;
        // TODO make inmutable copy
        this.logs = logs == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList<>(logs));
    }

    public long getGasUsed() {
        return this.gasUsed;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public List<Log> getLogs() { return this.logs; }

    public Hash getHash() {
        if (this.hash == null)
            this.hash = HashUtils.calculateHash(TransactionReceiptEncoder.encode(this));

        return this.hash;
    }
}

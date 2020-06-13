package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.vms.eth.Log;

import java.util.List;

/**
 * Created by ajlopez on 26/05/2020.
 */
public class TransactionReceiptEncoder {
    private TransactionReceiptEncoder() {}

    public static byte[] encode(TransactionReceipt transactionReceipt) {
        byte[] rlpGasUsed = RLPEncoder.encodeUnsignedLong(transactionReceipt.getGasUsed());
        byte[] rlpSuccess = RLPEncoder.encodeBoolean(transactionReceipt.getSuccess());
        byte[] rlpLogs = LogEncoder.encodeList(transactionReceipt.getLogs());

        return RLP.encodeList(rlpGasUsed, rlpSuccess, rlpLogs);
    }

    public static TransactionReceipt decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        if (bytes.length != 3)
            throw new IllegalArgumentException("Invalid transaction receipt encoding");

        long gasUsed = RLPEncoder.decodeUnsignedLong(bytes[0]);
        boolean success = RLPEncoder.decodeBoolean(bytes[1]);
        List<Log> logs = LogEncoder.decodeList(bytes[2]);

        return new TransactionReceipt(gasUsed, success, logs);
    }
}

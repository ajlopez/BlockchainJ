package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.vms.eth.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 06/06/2020.
 */
public class LogEncoder {
    private LogEncoder() { }

    public static byte[] encode(Log log) {
        byte[] rlpAddress = RLPEncoder.encodeAddress(log.getAddress());
        byte[] rlpData = RLP.encode(log.getData());
        byte[] rlpTopics = RLPEncoder.encodeDataWords(log.getTopics());

        return RLP.encodeList(rlpAddress, rlpData, rlpTopics);
    }

    public static Log decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        if (bytes.length != 3)
            throw new IllegalArgumentException("Invalid log encoding");

        Address address = RLPEncoder.decodeAddress(bytes[0]);
        byte[] data = RLP.decode(bytes[1]);
        List<DataWord> topics = RLPEncoder.decodeDataWords(bytes[2]);

        return new Log(address, data, topics);
    }

    public static byte[] encodeList(List<Log> logs) {
        byte[][] bytes = new byte[logs.size()][];

        for (int k = 0; k < logs.size(); k++)
            bytes[k] = encode(logs.get(k));

        return RLP.encodeList(bytes);
    }

    public static List<Log> decodeList(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);
        List<Log> logs = new ArrayList<>(bytes.length);

        for (int k = 0; k < bytes.length; k++)
            logs.add(decode(bytes[k]));

        return logs;
    }
}

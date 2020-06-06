package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.vms.eth.Log;

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

        // TODO check number of parts

        Address address = RLPEncoder.decodeAddress(bytes[0]);
        byte[] data = RLP.decode(bytes[1]);
        List<DataWord> topics = RLPEncoder.decodeDataWords(bytes[2]);

        return new Log(address, data, topics);
    }
}

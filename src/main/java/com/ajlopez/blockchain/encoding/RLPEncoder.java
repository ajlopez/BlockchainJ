package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 24/02/2019.
 */
public class RLPEncoder {
    private RLPEncoder() {}

    public static byte[] encodeAddress(Address address) {
        if (address == null)
            return RLP.encode(ByteUtils.EMPTY_BYTE_ARRAY);

        return RLP.encode(address.getBytes());
    }

    public static Address decodeAddress(byte[] data) {
        return new Address(RLP.decode(data));
    }

    public static byte[] encodeDataWord(DataWord value) { return RLP.encode(value.toNormalizedBytes()); }

    public static DataWord decodeDataWord(byte[] data) { return DataWord.fromBytes(RLP.decode(data)); }

    public static byte[] encodeDataWords(List<DataWord> values) {
        int nvalues = values.size();
        byte[][] bytes = new byte[nvalues][];

        for (int k = 0; k < nvalues; k++)
            bytes[k] = encodeDataWord(values.get(k));

        return RLP.encodeList(bytes);
    }

    public static List<DataWord> decodeDataWords(byte[] data) {
        byte[][] bytes = RLP.decodeList(data);
        int nvalues = bytes.length;
        List<DataWord> values = new ArrayList<>();

        for (int k = 0; k < nvalues; k++)
            values.add(decodeDataWord(bytes[k]));

        return values;
    }

    public static byte[] encodeCoin(Coin value) {
        return RLP.encode(value.toBytes());
    }

    public static Coin decodeCoin(byte[] data) {
        return Coin.fromBytes(RLP.decode(data));
    }

    public static byte[] encodeDifficulty(Difficulty value) {
        return RLP.encode(value.toBytes());
    }

    public static Difficulty decodeDifficulty(byte[] data) {
        return Difficulty.fromBytes(RLP.decode(data));
    }

    public static byte[] encodeUnsignedInteger(int value) {
        return RLP.encode(ByteUtils.unsignedIntegerToNormalizedBytes(value));
    }

    public static byte[] encodeUnsignedLong(long value) {
        return RLP.encode(ByteUtils.unsignedLongToNormalizedBytes(value));
    }

    public static int decodeUnsignedInteger(byte[] data) {
        return ByteUtils.bytesToUnsignedInteger(RLP.decode(data));
    }

    public static long decodeUnsignedLong(byte[] data) {
        return ByteUtils.bytesToUnsignedLong(RLP.decode(data));
    }

    public static byte[] encodeLong(long value) {
        return RLP.encode(ByteUtils.longToNormalizedBytes(value));
    }

    public static byte[] encodeNonce(long value) {
        return RLP.encode(ByteUtils.longToBytes(value));
    }

    public static long decodeLong(byte[] data) {
        return ByteUtils.bytesToLong(RLP.decode(data));
    }

    public static byte[] encodeBlockHash(BlockHash blockHash) {
        if (blockHash == null)
            return RLP.encode(ByteUtils.EMPTY_BYTE_ARRAY);

        return RLP.encode(blockHash.getBytes());
    }

    public static BlockHash decodeBlockHash(byte[] data) {
        byte[] bytes = RLP.decode(data);

        if (bytes.length == 0)
            return null;

        return new BlockHash(bytes);
    }

    public static byte[] encodeHash(Hash hash) {
        if (hash == null)
            return RLP.encode(ByteUtils.EMPTY_BYTE_ARRAY);

        return RLP.encode(hash.getBytes());
    }

    public static Hash decodeHash(byte[] data) {
        byte[] bytes = RLP.decode(data);

        if (bytes.length == 0)
            return null;

        return new Hash(bytes);
    }

    public static byte[] encodePeerId(PeerId peerId) {
        return RLP.encode(peerId.getBytes());
    }

    public static PeerId decodePeerId(byte[] data) {
        return new PeerId(RLP.decode(data));
    }

    public static byte[] encodeBoolean(Boolean value) {
        return RLP.encode(value ? new byte[] { 0x01 } : new byte[1]);
    }

    public static boolean decodeBoolean(byte[] data) {
        byte[] bytes = RLP.decode(data);

        if (bytes.length != 1 || (bytes[0] != 0x01 && bytes[0] != 0x00))
            throw new IllegalArgumentException("Invalid encoded boolean");

        return bytes[0] == 0x01;
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.encoding.RLP;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class RLPTest {
    @Test
    public void encodeSingleByte() {
        byte[] result = RLP.encode(new byte[] { 0x01 });

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(0x01, result[0]);
    }

    @Test
    public void encodeEmptyByteArray() {
        byte[] result = RLP.encode(new byte[0]);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals((byte)0x80, result[0]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeNullAsEmptyByteArray() {
        byte[] result = RLP.encode(null);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals((byte)0x80, result[0]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeSingleByteWithHighValue() {
        byte[] result = RLP.encode(new byte[] { (byte)0x80 });

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals((byte)0x81, result[0]);
        Assert.assertEquals((byte)0x80, result[1]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void decodeEmptyByteArray() {
        byte[] result = RLP.decode(new byte[] { (byte)0x80 });

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void encodeOneHundredByteArray() {
        byte[] bytes = new byte[100];

        for (int k = 0; k < 100; k++)
            bytes[k] = (byte)(k + 1);

        byte[] result = RLP.encode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(102, result.length);
        Assert.assertEquals((byte)184, result[0]);
        Assert.assertEquals(100, result[1]);

        for (int k = 0; k < 100; k++)
            Assert.assertEquals(bytes[k], result[k + 2]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeDecodeOneHundredByteArray() {
        byte[] bytes = new byte[100];

        for (int k = 0; k < 100; k++)
            bytes[k] = (byte)(k + 1);

        byte[] encoded = RLP.encode(bytes);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[] result = RLP.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void encodeTwoHundredFiftyFiveByteArray() {
        byte[] bytes = new byte[255];

        for (int k = 0; k < 255; k++)
            bytes[k] = (byte)(k + 1);

        byte[] result = RLP.encode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(257, result.length);
        Assert.assertEquals((byte)184, result[0]);
        Assert.assertEquals((byte)0xff, result[1]);

        for (int k = 0; k < 255; k++)
            Assert.assertEquals(bytes[k], result[k + 2]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeDecodeTwoHundredFiftyFiveByteArray() {
        byte[] bytes = new byte[255];

        for (int k = 0; k < 255; k++)
            bytes[k] = (byte)(k + 1);

        byte[] encoded = RLP.encode(bytes);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[] result = RLP.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void encodeThreeHundredByteArray() {
        byte[] bytes = new byte[300];

        for (int k = 0; k < 300; k++)
            bytes[k] = (byte)(k + 1);

        byte[] result = RLP.encode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(303, result.length);
        Assert.assertEquals((byte)185, result[0]);
        Assert.assertEquals(1, result[1]);
        Assert.assertEquals(300 % 256, result[2]);

        for (int k = 0; k < 300; k++)
            Assert.assertEquals(bytes[k], result[k + 3]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeDecodeThreeHundredByteArray() {
        byte[] bytes = new byte[300];

        for (int k = 0; k < 300; k++)
            bytes[k] = (byte)(k + 1);

        byte[] encoded = RLP.encode(bytes);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[] result = RLP.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void encodeSixtyFourKByteArray() {
        byte[] bytes = new byte[1024 * 64];

        for (int k = 0; k < 1024 * 64; k++)
            bytes[k] = (byte)(k + 1);

        byte[] result = RLP.encode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(1024 * 64 + 4, result.length);
        Assert.assertEquals((byte)186, result[0]);
        Assert.assertEquals(1, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(0, result[3]);

        for (int k = 0; k < 1024 * 64; k++)
            Assert.assertEquals(bytes[k], result[k + 4]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeDecodeSixtyFourKByteArray() {
        byte[] bytes = new byte[1024 * 64];

        for (int k = 0; k < 1024 * 64; k++)
            bytes[k] = (byte)(k + 1);

        byte[] encoded = RLP.encode(bytes);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[] result = RLP.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void encodeSixteenMByteArray() {
        byte[] bytes = new byte[1024 * 1024 * 16];

        for (int k = 0; k < 1024 * 1024 * 16; k++)
            bytes[k] = (byte)(k + 1);

        byte[] result = RLP.encode(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(1024 * 1024 * 16 + 5, result.length);
        Assert.assertEquals((byte)187, result[0]);
        Assert.assertEquals(1, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(0, result[3]);
        Assert.assertEquals(0, result[4]);

        for (int k = 0; k < 1024 * 1024 * 16; k++)
            Assert.assertEquals(bytes[k], result[k + 5]);

        Assert.assertEquals(result.length, RLP.getTotalLength(result, 0));
    }

    @Test
    public void encodeDecodeSixteenMByteArray() {
        byte[] bytes = new byte[1024 * 1024 * 16];

        for (int k = 0; k < 1024 * 1024 * 16; k++)
            bytes[k] = (byte)(k + 1);

        byte[] encoded = RLP.encode(bytes);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[] result = RLP.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void decodeSingleByte() {
        byte[] result = RLP.decode(new byte[] { (byte)0x01 });

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(0x01, result[0]);
    }

    @Test
    public void decodeSingleByteWithHighValue() {
        byte[] result = RLP.decode(new byte[] { (byte)0x81, (byte)0x80 });

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals((byte)0x80, result[0]);
    }

    @Test
    public void decodeTwoBytes() {
        byte[] result = RLP.decode(new byte[] { (byte)0x82, 0x01, 0x02 });

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals((byte)0x01, result[0]);
        Assert.assertEquals((byte)0x02, result[1]);
    }

    @Test
    public void encodeListWithOneShortElement() {
        byte[] element = RLP.encode(new byte[] { 0x01, 0x03 });
        byte[] encoded = RLP.encodeList(element);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + element.length, encoded.length);
        Assert.assertEquals((byte)(192 + element.length), encoded[0]);

        for (int k = 0; k < element.length; k++)
            Assert.assertEquals(element[k], encoded[k + 1]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void encodeListWithTwoShortElements() {
        byte[] element1 = RLP.encode(new byte[] { 0x01, 0x02 });
        byte[] element2 = RLP.encode(new byte[] { 0x03, 0x04 });
        byte[] encoded = RLP.encodeList(element1, element2);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + element1.length + element2.length, encoded.length);
        Assert.assertEquals((byte)(192 + element1.length + element2.length), encoded[0]);

        for (int k = 0; k < element1.length; k++)
            Assert.assertEquals(element1[k], encoded[k + 1]);

        for (int k = 0; k < element2.length; k++)
            Assert.assertEquals(element2[k], encoded[k + 1 + element1.length]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void encodeListWithTwoLongElementsHavingOneByteLength() {
        byte[] element1 = RLP.encode(new byte[100]);
        byte[] element2 = RLP.encode(new byte[100]);
        byte[] encoded = RLP.encodeList(element1, element2);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + 1 + element1.length + element2.length, encoded.length);
        Assert.assertEquals((byte)(247 + 1), encoded[0]);
        Assert.assertEquals((byte)(element1.length + element2.length), encoded[1]);

        for (int k = 0; k < element1.length; k++)
            Assert.assertEquals(element1[k], encoded[k + 2]);

        for (int k = 0; k < element2.length; k++)
            Assert.assertEquals(element2[k], encoded[k + 2 + element1.length]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void encodeListWithTwoLongElementsHavingOneByteLengthBorderCase() {
        byte[] element1 = RLP.encode(new byte[125]);
        byte[] element2 = RLP.encode(new byte[126]);
        byte[] encoded = RLP.encodeList(element1, element2);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + 1 + element1.length + element2.length, encoded.length);
        Assert.assertEquals((byte)(247 + 1), encoded[0]);
        Assert.assertEquals((byte)(255), encoded[1]);

        for (int k = 0; k < element1.length; k++)
            Assert.assertEquals(element1[k], encoded[k + 2]);

        for (int k = 0; k < element2.length; k++)
            Assert.assertEquals(element2[k], encoded[k + 2 + element1.length]);
    }

    @Test
    public void encodeListWithTwoLongElementsHavingTwoByteLength() {
        byte[] element1 = RLP.encode(new byte[126]);
        byte[] element2 = RLP.encode(new byte[126]);
        byte[] encoded = RLP.encodeList(element1, element2);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + 2 + element1.length + element2.length, encoded.length);
        Assert.assertEquals((byte)(247 + 2), encoded[0]);
        Assert.assertEquals((byte)(1), encoded[1]);
        Assert.assertEquals((byte)(0), encoded[2]);

        for (int k = 0; k < element1.length; k++)
            Assert.assertEquals(element1[k], encoded[k + 3]);

        for (int k = 0; k < element2.length; k++)
            Assert.assertEquals(element2[k], encoded[k + 3 + element1.length]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void encodeListWithOneLongElementHavingTwoByteLengthBorderCase() {
        byte[] element = RLP.encode(new byte[256 * 256 - 4]);
        byte[] encoded = RLP.encodeList(element);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + 2 + element.length, encoded.length);
        Assert.assertEquals((byte)(247 + 2), encoded[0]);
        Assert.assertEquals((byte)(255), encoded[1]);
        Assert.assertEquals((byte)(255), encoded[2]);

        for (int k = 0; k < element.length; k++)
            Assert.assertEquals(element[k], encoded[k + 3]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void encodeListWithLongElementHavingThreeByteLength() {
        byte[] element = RLP.encode(new byte[256 * 256 -3]);
        byte[] encoded = RLP.encodeList(element);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1 + 3 + element.length, encoded.length);
        Assert.assertEquals((byte)(247 + 3), encoded[0]);
        Assert.assertEquals((byte)(1), encoded[1]);
        Assert.assertEquals((byte)(0), encoded[2]);
        Assert.assertEquals((byte)(0), encoded[3]);

        for (int k = 0; k < element.length; k++)
            Assert.assertEquals(element[k], encoded[k + 4]);

        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));
    }

    @Test
    public void lengthToBytes() {
        Assert.assertArrayEquals(new byte[] { 0 }, RLP.lengthToBytes(0));
        Assert.assertArrayEquals(new byte[] { (byte)255 }, RLP.lengthToBytes(255));
        Assert.assertArrayEquals(new byte[] { 1, 0 }, RLP.lengthToBytes(256));
        Assert.assertArrayEquals(new byte[] { 1, (byte) 255 }, RLP.lengthToBytes(256 + 255));
        Assert.assertArrayEquals(new byte[] { 1, 0, 0 }, RLP.lengthToBytes(256 * 256));
        Assert.assertArrayEquals(new byte[] { 1, 0, 0, 0 }, RLP.lengthToBytes(256 * 256 * 256));
    }

    @Test
    public void bytesToLength() {
        Assert.assertEquals(0, RLP.bytesToLength(new byte[] { 0 }));
        Assert.assertEquals(255, RLP.bytesToLength(new byte[] { (byte)255 }));
        Assert.assertEquals(256, RLP.bytesToLength(new byte[] { 1, 0 }));
        Assert.assertEquals(256 * 256, RLP.bytesToLength(new byte[] { 1, 0, 0 }));
        Assert.assertEquals(256 * 256 * 256, RLP.bytesToLength(new byte[] { 1, 0, 0, 0 }));
    }

    @Test
    public void encodeDecodeListWithOneShortElement() {
        byte[] bytes = new byte[] { 0x01, 0x03 };
        byte[] element = RLP.encode(bytes);
        byte[] encoded = RLP.encodeList(element);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[][] result = RLP.decodeList(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
        Assert.assertArrayEquals(element, result[0]);

        Assert.assertEquals(1 + element.length, encoded.length);
        Assert.assertEquals((byte)(192 + element.length), encoded[0]);

        for (int k = 0; k < element.length; k++)
            Assert.assertEquals(element[k], encoded[k + 1]);
    }

    @Test
    public void encodeDecodeListWithTwoElements() {
        byte[] bytes1 = new byte[100];
        byte[] bytes2 = new byte[100];

        byte[] element1 = RLP.encode(bytes1);
        byte[] element2 = RLP.encode(bytes2);
        byte[] encoded = RLP.encodeList(element1, element2);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(encoded.length, RLP.getTotalLength(encoded, 0));

        byte[][] result = RLP.decodeList(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.length);
        Assert.assertArrayEquals(element1, result[0]);
        Assert.assertArrayEquals(element2, result[1]);

        Assert.assertEquals(2 + element1.length + element2.length, encoded.length);
    }
}

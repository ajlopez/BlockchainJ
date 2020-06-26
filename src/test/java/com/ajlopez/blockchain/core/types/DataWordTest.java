package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWordTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createDataWord() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];
        random.nextBytes(bytes);

        DataWord dataWord = new DataWord(bytes);

        Assert.assertArrayEquals(bytes, dataWord.getBytes());
    }

    @Test
    public void dataWordToString() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];
        random.nextBytes(bytes);

        DataWord dataWord = new DataWord(bytes);

        String expected = HexUtils.bytesToHexString(bytes, true);
        Assert.assertEquals(expected, dataWord.toString());
    }

    @Test
    public void tooLargeByteArray() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES + 1];
        random.nextBytes(bytes);
        bytes[0] = 1;

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Too large byte array");
        new DataWord(bytes);
    }

    @Test
    public void nullByteArrayInConstructor() {
        try {
            new DataWord(null);
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Null byte array", ex.getMessage());
        }
    }

    @Test
    public void dataWordsWithTheSameBytesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];
        random.nextBytes(bytes);

        DataWord dataWord1 = new DataWord(bytes);
        DataWord dataWord2 = new DataWord(bytes);

        Assert.assertEquals(dataWord1, dataWord2);
        Assert.assertTrue(dataWord1.equals(dataWord2));
        Assert.assertTrue(dataWord2.equals(dataWord1));
        Assert.assertEquals(dataWord1.hashCode(), dataWord2.hashCode());
    }

    @Test
    public void dataWordsWithTheSameBytesValuesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];
        random.nextBytes(bytes);
        byte[] bytes2 = Arrays.copyOf(bytes, bytes.length);

        DataWord dataWord1 = new DataWord(bytes);
        DataWord dataWord2 = new DataWord(bytes2);

        Assert.assertEquals(dataWord1, dataWord2);
        Assert.assertTrue(dataWord1.equals(dataWord2));
        Assert.assertTrue(dataWord2.equals(dataWord1));
        Assert.assertEquals(dataWord1.hashCode(), dataWord2.hashCode());
    }

    @Test
    public void notEqual() {
        Random random = new Random();
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];
        random.nextBytes(bytes);

        DataWord dataWord = new DataWord(bytes);

        Assert.assertFalse(dataWord.equals(null));
        Assert.assertFalse(dataWord.equals("foo"));
        Assert.assertFalse(dataWord.equals(new Hash(bytes)));
        Assert.assertFalse(dataWord.equals(new BlockHash(bytes)));
    }

    @Test
    public void addTwoShortDataWords() {
        DataWord word1 = DataWord.fromHexadecimalString("0103");
        DataWord word2 = DataWord.fromHexadecimalString("010305");

        DataWord result = word1.add(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x010408", result.toNormalizedString());
    }

    @Test
    public void addTwoShortDataWordsWithOverflow() {
        DataWord word1 = DataWord.fromHexadecimalString("ff");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.add(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x0100", result.toNormalizedString());
    }

    @Test
    public void addTwoDataWordsWithFullOverflow() {
        DataWord word1 = DataWord.fromHexadecimalString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.add(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x00", result.toNormalizedString());
    }

    @Test
    public void subtractTwoShortDataWords() {
        DataWord word1 = DataWord.fromHexadecimalString("0100");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.sub(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0xff", result.toNormalizedString());
    }

    @Test
    public void subtractTwoDataWords() {
        DataWord word1 = DataWord.fromHexadecimalString("0101");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.sub(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x0100", result.toNormalizedString());
    }

    @Test
    public void subtractOneFromZero() {
        DataWord word1 = DataWord.fromHexadecimalString("00");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.sub(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", result.toNormalizedString());
    }

    @Test
    public void subtractTwoFromZero() {
        DataWord word1 = DataWord.fromHexadecimalString("00");
        DataWord word2 = DataWord.fromHexadecimalString("02");

        DataWord result = word1.sub(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe", result.toNormalizedString());
    }

    @Test
    public void executeExpOperations() {
        executeExp("01", "02", "01");
        executeExp("02", "02", "04");
        executeExp("0100", "04", "0100000000");
        executeExp("02", "10", "010000");
        executeExp("02", "0100", "00");
        executeExp("03", "02", "09");
        executeExp("03", "00", "01");
        executeExp("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "02", "01");
        executeExp("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe", "02", "04");
    }

    @Test
    public void compareDataWords() {
        DataWord word1 = DataWord.fromUnsignedInteger(1);
        DataWord word2 = DataWord.fromUnsignedInteger(42);
        DataWord word3 = DataWord.fromHexadecimalString("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        DataWord word4 = DataWord.ONE;

        Assert.assertTrue(word1.compareTo(word1) == 0);
        Assert.assertTrue(word2.compareTo(word2) == 0);
        Assert.assertTrue(word3.compareTo(word3) == 0);

        Assert.assertTrue(word1.compareTo(word4) == 0);
        Assert.assertTrue(word1.compareTo(word2) < 0);
        Assert.assertTrue(word1.compareTo(word3) < 0);

        Assert.assertTrue(word2.compareTo(word4) > 0);
        Assert.assertTrue(word2.compareTo(word1) > 0);
        Assert.assertTrue(word2.compareTo(word3) < 0);

        Assert.assertTrue(word3.compareTo(word4) > 0);
        Assert.assertTrue(word3.compareTo(word1) > 0);
        Assert.assertTrue(word3.compareTo(word2) > 0);
    }

    @Test
    public void compareDataWordAndHash() {
        DataWord word1 = DataWord.fromUnsignedInteger(1);
        DataWord word2 = DataWord.TWO;
        Hash hash1 = new Hash(word1.getBytes());
        Hash hash2 = new Hash(DataWord.fromUnsignedInteger(42).getBytes());
        Hash hash3 = new Hash(DataWord.fromHexadecimalString("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff").getBytes());
        Hash hash4 = new Hash(DataWord.ONE.getBytes());

        Assert.assertTrue(word1.compareTo(word1) == 0);
        Assert.assertTrue(word1.compareTo(hash1) == 0);

        Assert.assertTrue(word1.compareTo(hash4) == 0);
        Assert.assertTrue(word2.compareTo(hash4) > 0);
        Assert.assertTrue(word1.compareTo(hash2) < 0);
        Assert.assertTrue(word1.compareTo(hash3) < 0);
    }

    @Test
    public void executeOrOperations() {
        executeOr("01", "01", "01");
        executeOr("02", "04", "06");
        executeOr("ff", "ff00", "ffff");
    }

    @Test
    public void executeAndOperations() {
        executeAnd("01", "01", "01");
        executeAnd("02", "04", "00");
        executeAnd("03", "05", "01");
        executeAnd("ff", "ff00", "00");
        executeAnd("ffff", "ffff", "ffff");
        executeAnd("ffffff", "ff00", "ff00");
    }

    @Test
    public void executeXorOperations() {
        executeXor("01", "01", "00");
        executeXor("02", "04", "06");
        executeXor("03", "05", "06");
        executeXor("ff", "ff00", "ffff");
        executeXor("ffff", "ffff", "00");
        executeXor("ffffff", "ff00", "ff00ff");
    }

    @Test
    public void executeNotOperations() {
        executeNot("00", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        executeNot("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "00");
        executeNot("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00", "ff");
    }

    @Test
    public void executeNegateOperations() {
        executeNegate("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        executeNegate("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "01");
        executeNegate("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe", "02");
    }

    @Test
    public void isUnsignedInteger() {
        Assert.assertTrue(DataWord.fromHexadecimalString("00").isUnsignedInteger());
        Assert.assertTrue(DataWord.fromHexadecimalString("0100").isUnsignedInteger());
        Assert.assertTrue(DataWord.fromHexadecimalString("7fffffff").isUnsignedInteger());

        Assert.assertFalse(DataWord.fromHexadecimalString("ffffffff").isUnsignedInteger());
        Assert.assertFalse(DataWord.fromHexadecimalString("0100000000").isUnsignedInteger());
    }

    @Test
    public void isZero() {
        Assert.assertTrue(DataWord.fromHexadecimalString("00").isZero());

        Assert.assertFalse(DataWord.fromHexadecimalString("0100").isZero());
        Assert.assertFalse(DataWord.fromHexadecimalString("7fffffff").isZero());
        Assert.assertFalse(DataWord.fromHexadecimalString("ffffffff").isZero());
        Assert.assertFalse(DataWord.fromHexadecimalString("0100000000").isZero());
    }

    @Test
    public void isNegative() {
        Assert.assertTrue(DataWord.fromUnsignedLong(1).negate().isNegative());
        Assert.assertTrue(DataWord.fromUnsignedLong(42).negate().isNegative());
        Assert.assertTrue(DataWord.fromUnsignedLong(1024).negate().isNegative());

        Assert.assertFalse(DataWord.ZERO.isNegative());
        Assert.assertFalse(DataWord.fromUnsignedLong(1).isNegative());
        Assert.assertFalse(DataWord.fromUnsignedLong(42).isNegative());
        Assert.assertFalse(DataWord.fromUnsignedLong(1024).isNegative());
    }

    @Test
    public void fromAddress() {
        Address address = FactoryHelper.createRandomAddress();

        DataWord word = DataWord.fromAddress(address);

        Assert.assertArrayEquals(ByteUtils.normalizedBytes(address.getBytes()), word.toNormalizedBytes());
    }

    @Test
    public void fromEmptyBytes() {
        DataWord word = DataWord.fromBytes(new byte[0], 0, 0);

        Assert.assertEquals(DataWord.ZERO, word);
    }

    @Test
    public void fromOneByte() {
        DataWord word = DataWord.fromBytes(new byte[] { (byte)0xff }, 0, 1);

        Assert.assertEquals(DataWord.fromUnsignedInteger(255), word);
    }

    @Test
    public void maximumValueFromBytes() {
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES];

        for (int k = 0; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        DataWord word = DataWord.fromBytes(bytes, 0, bytes.length);

        Assert.assertEquals(DataWord.fromBigInteger(new BigInteger(1, bytes)), word);
    }

    @Test
    public void cannotCreateFromTooManyBytes() {
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES + 1];

        bytes[0] = 1;

        for (int k = 1; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Too large byte array");
        DataWord.fromBytes(bytes, 0, bytes.length);
    }

    @Test
    public void maximumValueFromTooManyBytes() {
        byte[] bytes = new byte[DataWord.DATAWORD_BYTES + 1];

        for (int k = 1; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        DataWord word = DataWord.fromBytes(bytes, 0, bytes.length);

        Assert.assertEquals(DataWord.fromBigInteger(new BigInteger(1, bytes)), word);
    }

    @Test
    public void toAddress() {
        byte[] bytes = FactoryHelper.createRandomBytes(DataWord.DATAWORD_BYTES);

        DataWord word = DataWord.fromBytes(bytes, 0, bytes.length);

        Address address = word.toAddress();

        Assert.assertNotNull(address);

        byte[] addrbytes = address.getBytes();
        byte[] wordbytes = word.getBytes();

        for (int k = 0; k < Address.ADDRESS_BYTES; k++)
            Assert.assertEquals(wordbytes[k + DataWord.DATAWORD_BYTES - Address.ADDRESS_BYTES], addrbytes[k]);
    }

    private static void executeNegate(String operand, String expected) {
        DataWord word = DataWord.fromHexadecimalString(operand);

        DataWord result = word.negate();

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }

    private static void executeNot(String operand, String expected) {
        DataWord word = DataWord.fromHexadecimalString(operand);

        DataWord result = word.not();

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }

    private static void executeOr(String operand1, String operand2, String expected) {
        DataWord word1 = DataWord.fromHexadecimalString(operand1);
        DataWord word2 = DataWord.fromHexadecimalString(operand2);

        DataWord result = word1.or(word2);

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }

    private static void executeAnd(String operand1, String operand2, String expected) {
        DataWord word1 = DataWord.fromHexadecimalString(operand1);
        DataWord word2 = DataWord.fromHexadecimalString(operand2);

        DataWord result = word1.and(word2);

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }

    private static void executeXor(String operand1, String operand2, String expected) {
        DataWord word1 = DataWord.fromHexadecimalString(operand1);
        DataWord word2 = DataWord.fromHexadecimalString(operand2);

        DataWord result = word1.xor(word2);

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }

    private static void executeExp(String operand1, String operand2, String expected) {
        DataWord word1 = DataWord.fromHexadecimalString(operand1);
        DataWord word2 = DataWord.fromHexadecimalString(operand2);

        DataWord result = word1.exp(word2);

        Assert.assertEquals(DataWord.fromHexadecimalString(expected), result);
    }
}

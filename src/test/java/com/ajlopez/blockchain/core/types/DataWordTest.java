package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWordTest {
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

        try {
            new DataWord(bytes);
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Too large byte array", ex.getMessage());
        }
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
    public void isUnsignedInteger() {
        Assert.assertTrue(DataWord.fromHexadecimalString("00").isUnsignedInteger());
        Assert.assertTrue(DataWord.fromHexadecimalString("0100").isUnsignedInteger());
        Assert.assertTrue(DataWord.fromHexadecimalString("7fffffff").isUnsignedInteger());

        Assert.assertFalse(DataWord.fromHexadecimalString("ffffffff").isUnsignedInteger());
        Assert.assertFalse(DataWord.fromHexadecimalString("0100000000").isUnsignedInteger());
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
}

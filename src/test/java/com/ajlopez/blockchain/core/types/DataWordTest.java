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
    public void addTwoShortDataWordsWithFullOverflow() {
        DataWord word1 = DataWord.fromHexadecimalString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        DataWord word2 = DataWord.fromHexadecimalString("01");

        DataWord result = word1.add(word2);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x00", result.toNormalizedString());
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.DataWord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/05/2020.
 */
public class RLPEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeTrue() {
        byte[] encoded = RLPEncoder.encodeBoolean(true);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1, encoded.length);
        Assert.assertEquals(1, encoded[0]);

        Assert.assertTrue(RLPEncoder.decodeBoolean(encoded));
    }

    @Test
    public void encodeDecodeFalse() {
        byte[] encoded = RLPEncoder.encodeBoolean(false);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1, encoded.length);
        Assert.assertEquals(0, encoded[0]);

        Assert.assertFalse(RLPEncoder.decodeBoolean(encoded));
    }

    @Test
    public void decodeBooleanIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid encoded boolean");
        RLPEncoder.decodeBoolean(new byte[] { 0x02 });
    }

    @Test
    public void encodeDecodeDataWordOne() {
        byte[] encoded = RLPEncoder.encodeDataWord(DataWord.ONE);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(1, encoded.length);
        Assert.assertEquals(0x01, encoded[0]);

        DataWord result = RLPEncoder.decodeDataWord(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void encodeDecodeMaxDataWord() {
        byte[] encoded = RLPEncoder.encodeDataWord(DataWord.MAX);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(DataWord.DATAWORD_BYTES + 1, encoded.length);

        DataWord result = RLPEncoder.decodeDataWord(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.MAX, result);
    }
}

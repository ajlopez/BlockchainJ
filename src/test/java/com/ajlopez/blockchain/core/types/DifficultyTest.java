package com.ajlopez.blockchain.core.types;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/08/2019.
 */
public class DifficultyTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createUsingBigInteger() {
        Difficulty difficulty = new Difficulty(BigInteger.TEN);

        Assert.assertEquals(BigInteger.TEN, difficulty.asBigInteger());
    }

    @Test
    public void createUsingUnsignedLong() {
        Difficulty difficulty = Difficulty.fromUnsignedLong(10);

        Assert.assertEquals(BigInteger.TEN, difficulty.asBigInteger());
    }

    @Test
    public void cannotCreateWithANegativeLong() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");

        Difficulty.fromUnsignedLong(-10);
    }

    @Test
    public void createUsingBytes() {
        Difficulty difficulty = Difficulty.fromBytes(new byte[] { (byte)0xff, (byte)0xff });

        Assert.assertEquals(BigInteger.valueOf(0x0000ffff), difficulty.asBigInteger());
    }

    @Test
    public void createUsingEmptyBytes() {
        Difficulty difficulty = Difficulty.fromBytes(new byte[0]);

        Assert.assertEquals(BigInteger.ZERO, difficulty.asBigInteger());
    }

    @Test
    public void cannotCreateUsingTooMuchBytes() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value is too big");

        byte[] bytes = new byte[NaturalValue.NATURAL_VALUE_BYTES + 1];
        bytes[0] = (byte)0xff;

        Difficulty.fromBytes(bytes);
    }

    @Test
    public void createMaximumValueUsingBytes() {
        byte[] bytes = new byte[NaturalValue.NATURAL_VALUE_BYTES];

        for (int k = 0; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        Difficulty difficulty = Difficulty.fromBytes(bytes);

        Assert.assertEquals(new BigInteger(1, bytes), difficulty.asBigInteger());
    }

    @Test
    public void createUsingBigIntegerZero() {
        Difficulty difficulty = new Difficulty(BigInteger.ZERO);

        Assert.assertEquals(BigInteger.ZERO, difficulty.asBigInteger());
    }

    @Test
    public void createUsingLongZero() {
        Difficulty difficulty = Difficulty.fromUnsignedLong(0);

        Assert.assertEquals(BigInteger.ZERO, difficulty.asBigInteger());
    }

    @Test
    public void cannotCreateUsingNegativeBigInteger() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        new Difficulty(BigInteger.TEN.negate());
    }

    @Test
    public void cannotCreateUsingNegativeLong() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        Difficulty.fromUnsignedLong(-10);
    }

    @Test
    public void equalsNotEquals() {
        Assert.assertEquals(new Difficulty(BigInteger.TEN), new Difficulty(BigInteger.TEN));

        Assert.assertNotEquals(new Difficulty(BigInteger.TEN), new NaturalValue(BigInteger.TEN));
        Assert.assertNotEquals(new Difficulty(BigInteger.TEN), BigInteger.TEN);
        Assert.assertNotEquals(new Difficulty(BigInteger.TEN), null);
    }

    @Test
    public void compareHashCodes() {
        Assert.assertEquals(new Difficulty(BigInteger.TEN).hashCode(), new Difficulty(BigInteger.TEN).hashCode());
        Assert.assertEquals(new Difficulty(BigInteger.TEN).hashCode(), new NaturalValue(BigInteger.TEN).hashCode());
        Assert.assertEquals(new Difficulty(BigInteger.TEN).hashCode(), BigInteger.TEN.hashCode());

        Assert.assertNotEquals(new Difficulty(BigInteger.TEN).hashCode(), new Difficulty(BigInteger.ONE));
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, Difficulty.ONE.compareTo(Difficulty.ONE));
        Assert.assertEquals(-1, Difficulty.ONE.compareTo(Difficulty.TWO));
        Assert.assertEquals(1, Difficulty.TWO.compareTo(Difficulty.ONE));
    }
    
    @Test
    public void isZero() {
        Assert.assertTrue(Difficulty.ZERO.isZero());
        Assert.assertFalse(Difficulty.ONE.isZero());
        Assert.assertFalse(Difficulty.fromUnsignedLong(42).isZero());
    }

    @Test
    public void difficultyTwoToTarget() {
        DataWord result = Difficulty.TWO.toTarget();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.DATAWORD_BYTES, result.getBytes().length);
        Assert.assertEquals((byte)0x80, result.getBytes()[0]);

        DataWord expected = DataWord.fromBigInteger(BigInteger.valueOf(2).pow(255));

        Assert.assertEquals(expected, result);
    }
}

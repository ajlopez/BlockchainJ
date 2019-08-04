package com.ajlopez.blockchain.core.types;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class CoinTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createUsingBigInteger() {
        Coin coin = new Coin(BigInteger.TEN);

        Assert.assertEquals(BigInteger.TEN, coin.asBigInteger());
    }

    @Test
    public void createUsingUnsignedLong() {
        Coin coin = Coin.fromUnsignedLong(10);

        Assert.assertEquals(BigInteger.TEN, coin.asBigInteger());
    }

    @Test
    public void createUsingBytes() {
        Coin coin = Coin.fromBytes(new byte[] { (byte)0xff, (byte)0xff });

        Assert.assertEquals(BigInteger.valueOf(0x0000ffff), coin.asBigInteger());
    }

    @Test
    public void createUsingEmptyBytes() {
        Coin coin = Coin.fromBytes(new byte[0]);

        Assert.assertEquals(BigInteger.ZERO, coin.asBigInteger());
    }

    @Test
    public void cannotCreateUsingTooMuchBytes() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value is too big");

        byte[] bytes = new byte[NaturalValue.NATURAL_VALUE_BYTES + 1];
        bytes[0] = (byte)0xff;

        Coin.fromBytes(bytes);
    }

    @Test
    public void createMaximumValueUsingBytes() {
        byte[] bytes = new byte[NaturalValue.NATURAL_VALUE_BYTES];

        for (int k = 0; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        Coin coin = Coin.fromBytes(bytes);

        Assert.assertEquals(new BigInteger(1, bytes), coin.asBigInteger());
    }

    @Test
    public void createUsingBigIntegerZero() {
        Coin coin = new Coin(BigInteger.ZERO);

        Assert.assertEquals(BigInteger.ZERO, coin.asBigInteger());
    }

    @Test
    public void createUsingLongZero() {
        Coin coin = Coin.fromUnsignedLong(0);

        Assert.assertEquals(BigInteger.ZERO, coin.asBigInteger());
    }

    @Test
    public void cannotCreateUsingNegativeBigInteger() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        new Coin(BigInteger.TEN.negate());
    }

    @Test
    public void cannotCreateUsingNegativeLong() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        Coin.fromUnsignedLong(-10);
    }

    @Test
    public void equalsNotEquals() {
        Assert.assertEquals(new Coin(BigInteger.TEN), new Coin(BigInteger.TEN));

        Assert.assertNotEquals(new Coin(BigInteger.TEN), new NaturalValue(BigInteger.TEN));
        Assert.assertNotEquals(new Coin(BigInteger.TEN), BigInteger.TEN);
        Assert.assertNotEquals(new Coin(BigInteger.TEN), null);
    }

    @Test
    public void compareHashCodes() {
        Assert.assertEquals(new Coin(BigInteger.TEN).hashCode(), new Coin(BigInteger.TEN).hashCode());
        Assert.assertEquals(new Coin(BigInteger.TEN).hashCode(), new NaturalValue(BigInteger.TEN).hashCode());
        Assert.assertEquals(new Coin(BigInteger.TEN).hashCode(), BigInteger.TEN.hashCode());

        Assert.assertNotEquals(new Coin(BigInteger.TEN).hashCode(), new Coin(BigInteger.ONE));
    }

    @Test
    public void addCoin() {
        Assert.assertEquals(Coin.TWO, Coin.ONE.add(Coin.ONE));
        Assert.assertEquals(Coin.ZERO, Coin.ZERO.add(Coin.ZERO));
        Assert.assertEquals(Coin.fromUnsignedLong(42), Coin.fromUnsignedLong(2).add(Coin.fromUnsignedLong(40)));
    }

    @Test
    public void subtractCoin() {
        Assert.assertEquals(Coin.ONE, Coin.TWO.subtract(Coin.ONE));
        Assert.assertEquals(Coin.ZERO, Coin.ZERO.subtract(Coin.ZERO));
        Assert.assertEquals(Coin.fromUnsignedLong(42), Coin.fromUnsignedLong(44).subtract(Coin.fromUnsignedLong(2)));
    }

    @Test
    public void multiplyCoinByNonNegativeLongValue() {
        Assert.assertEquals(Coin.TWO, Coin.ONE.multiply(2));
        Assert.assertEquals(Coin.ZERO, Coin.ZERO.multiply(10));
        Assert.assertEquals(Coin.fromUnsignedLong(42), Coin.TWO.multiply(21));
    }

    @Test
    public void cannotSubtractToGiveNegativeValue() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        Coin.ONE.subtract(Coin.TWO);
    }

    @Test
    public void cannotMultiplyByNegativeLongValue() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        Coin.ONE.multiply(-2);
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, Coin.ONE.compareTo(Coin.ONE));
        Assert.assertEquals(-1, Coin.ONE.compareTo(Coin.TWO));
        Assert.assertEquals(1, Coin.TWO.compareTo(Coin.ONE));
    }
    
    @Test
    public void isZero() {
        Assert.assertTrue(Coin.ZERO.isZero());
        Assert.assertFalse(Coin.ONE.isZero());
        Assert.assertFalse(Coin.fromUnsignedLong(42).isZero());
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Bloom;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ajlopez on 21/04/2020.
 */
public class BloomEncoderTest {
    private Random random = new Random();

    @Test
    public void encodeDecodeEmptyBloom() {
        Bloom bloom = new Bloom();

        byte[] encoded = BloomEncoder.encode(bloom);

        Assert.assertNotNull(encoded);

        Bloom result = BloomEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void encodeDecodeRandomBloom() {
        Bloom bloom = FactoryHelper.createRandomBloom(200);

        byte[] encoded = BloomEncoder.encode(bloom);

        Assert.assertNotNull(encoded);

        Bloom result = BloomEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeEmptyBloomUsingNonZeroAlgorithm() {
        Bloom bloom = new Bloom();

        byte[] encoded = BloomEncoder.encodeNonZero(bloom);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(0, encoded.length);

        Bloom result = BloomEncoder.decodeNonZero(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertTrue(bloom.include(result));
        Assert.assertTrue(result.include(bloom));
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeBloomSizeOneUsingNonZeroAlgorithm() {
        Bloom bloom = new Bloom();
        bloom.add(2);

        byte[] encoded = BloomEncoder.encodeNonZero(bloom);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(2, encoded.length);
        Assert.assertEquals(0, encoded[0]);
        Assert.assertEquals(32, encoded[1]);

        Bloom result = BloomEncoder.decodeNonZero(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertTrue(bloom.include(result));
        Assert.assertTrue(result.include(bloom));
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeBloomSizeOneLastBitUsingNonZeroAlgorithm() {
        Bloom bloom = new Bloom();
        bloom.add(Bloom.BLOOM_BITS - 1);

        byte[] encoded = BloomEncoder.encodeNonZero(bloom);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(2, encoded.length);
        Assert.assertEquals(Bloom.BLOOM_BYTES - 1, encoded[0] & 0xff);
        Assert.assertEquals(1, encoded[1]);

        Bloom result = BloomEncoder.decodeNonZero(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertTrue(bloom.include(result));
        Assert.assertTrue(result.include(bloom));
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeBloomSizeTwoUsingNonZeroAlgorithm() {
        Bloom bloom = new Bloom();
        bloom.add(2);
        bloom.add(10);

        byte[] encoded = BloomEncoder.encodeNonZero(bloom);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(4, encoded.length);
        Assert.assertEquals(0, encoded[0]);
        Assert.assertEquals(32, encoded[1]);
        Assert.assertEquals(1, encoded[2]);
        Assert.assertEquals(32, encoded[3]);

        Bloom result = BloomEncoder.decodeNonZero(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertTrue(bloom.include(result));
        Assert.assertTrue(result.include(bloom));
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeBloomWithLastBitOnUsingNonZeroAlgorithm() {
        Bloom bloom = new Bloom();
        bloom.add(Bloom.BLOOM_BITS - 1);

        byte[] encoded = BloomEncoder.encodeNonZero(bloom);

        Assert.assertNotNull(encoded);
        Assert.assertEquals(2, encoded.length);
        Assert.assertEquals(255, encoded[0] & 0xff);
        Assert.assertEquals(1, encoded[1]);

        Bloom result = BloomEncoder.decodeNonZero(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(bloom.size(), result.size());
        Assert.assertTrue(bloom.include(result));
        Assert.assertTrue(result.include(bloom));
        Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
    }

    @Test
    public void encodeDecodeRandomBloomsUsingNonZeroAlgorithm() {
        for (int k = 0; k < 1000; k++) {
            Bloom bloom = FactoryHelper.createRandomBloom(random.nextInt(Bloom.BLOOM_BITS));

            byte[] encoded = BloomEncoder.encodeNonZero(bloom);

            Assert.assertNotNull(encoded);

            Bloom result = BloomEncoder.decodeNonZero(encoded);

            Assert.assertNotNull(result);
            Assert.assertEquals(bloom.size(), result.size());
            Assert.assertTrue(bloom.include(result));
            Assert.assertTrue(result.include(bloom));
            Assert.assertArrayEquals(bloom.getBytes(), result.getBytes());
        }
    }
}

package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.Bloom;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 21/04/2020.
 */
public class BloomEncoderTest {
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
}

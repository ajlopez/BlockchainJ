package com.ajlopez.blockchain.core.types;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

/**
 * Created by ajlopez on 10/04/2020.
 */
public class BloomTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createEmptyBloom() {
        Bloom bloom = new Bloom();

        Assert.assertEquals(0, bloom.size());
        Assert.assertTrue(bloom.include(bloom));
    }

    @Test
    public void include() {
        Bloom bloom1 = new Bloom();
        bloom1.add(1);
        bloom1.add(42);
        bloom1.add(100);

        Bloom bloom2 = new Bloom();
        bloom1.add(42);

        Assert.assertTrue(bloom1.include(bloom1));
        Assert.assertTrue(bloom2.include(bloom2));
        Assert.assertTrue(bloom1.include(bloom2));
        Assert.assertFalse(bloom2.include(bloom1));
    }

    @Test
    public void includeWithRandomElements() {
        Random random = new Random();

        Bloom bloom1 = new Bloom();
        Bloom bloom2 = new Bloom();

        for (int k = 0; k < Bloom.BLOOM_BITS / 4; k++) {
            int nelement = random.nextInt(Bloom.BLOOM_BITS);

            bloom1.add(nelement);
            bloom2.add(nelement);
        }

        for (int k = 0; k < Bloom.BLOOM_BITS / 4; k++)
            bloom1.add(random.nextInt(Bloom.BLOOM_BITS));

        Assert.assertTrue(bloom1.include(bloom1));
        Assert.assertTrue(bloom2.include(bloom2));
        Assert.assertTrue(bloom1.include(bloom2));
        Assert.assertFalse(bloom2.include(bloom1));
    }

    @Test
    public void addElement() {
        Bloom bloom = new Bloom();

        bloom.add(42);

        Assert.assertEquals(1, bloom.size());
    }

    @Test
    public void addInvalidElement() {
        Bloom bloom = new Bloom();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid bloom element");
        bloom.add(Bloom.BLOOM_BITS);
    }

    @Test
    public void addInvalidNegativeElement() {
        Bloom bloom = new Bloom();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid bloom element");
        bloom.add(-1);
    }

    @Test
    public void addAllElements() {
        Bloom bloom = new Bloom();

        for (int k = 0; k < Bloom.BLOOM_BYTES * 8; k++) {
            bloom.add(k);
            Assert.assertEquals(1 + k, bloom.size());
        }
    }
}

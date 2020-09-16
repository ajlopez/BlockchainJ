package com.ajlopez.blockchain.math.ec;

/**
 * Created by ajlopez on 16/09/2020.
 */
public class FieldElement {
    private final long prime;
    private final long value;

    public FieldElement(long prime, long value) {
        this.prime = prime;
        this.value = value;
    }

    public FieldElement add(FieldElement element) {
        return new FieldElement(this.prime, (this.value + element.value) % this.prime);
    }

    public FieldElement multiply(FieldElement element) {
        return new FieldElement(this.prime, (this.value * element.value) % this.prime);
    }

    public long toLong() {
        return this.value;
    }
}

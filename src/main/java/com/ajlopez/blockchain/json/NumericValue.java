package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class NumericValue extends Value {
    public NumericValue(String value) {
        super(ValueType.NUMBER, value);
    }
}

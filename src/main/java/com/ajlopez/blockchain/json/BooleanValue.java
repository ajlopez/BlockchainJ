package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class BooleanValue extends Value {
    public BooleanValue(boolean value) {
        super(ValueType.BOOLEAN, value);
    }
}

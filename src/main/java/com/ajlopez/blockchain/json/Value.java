package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public abstract class Value {
    private ValueType type;
    private Object value;

    public Value(ValueType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public ValueType getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }
}

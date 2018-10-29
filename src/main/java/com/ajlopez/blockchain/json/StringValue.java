package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class StringValue extends Value {
    public StringValue(String value) {
        super(ValueType.STRING, value);
    }

    @Override
    public String toString() {
        return "\"" + this.getValue() + "\"";
    }
}

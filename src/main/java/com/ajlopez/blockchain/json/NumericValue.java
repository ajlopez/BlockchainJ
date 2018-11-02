package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class NumericValue extends JsonValue {
    public NumericValue(String value) {
        super(ValueType.NUMBER, value);
    }

    @Override
    public String toString() {
        return (String)this.getValue();
    }
}

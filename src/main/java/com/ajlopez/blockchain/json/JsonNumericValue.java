package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonNumericValue extends JsonValue {
    public JsonNumericValue(String value) {
        super(ValueType.NUMBER, value);
    }

    @Override
    public String toString() {
        return (String)this.getValue();
    }
}

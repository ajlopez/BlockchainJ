package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class BooleanValue extends JsonValue {
    public BooleanValue(boolean value) {
        super(ValueType.BOOLEAN, value);
    }

    @Override
    public String toString() {
        if (this.getValue().equals(true))
            return "true";

        return "false";
    }
}

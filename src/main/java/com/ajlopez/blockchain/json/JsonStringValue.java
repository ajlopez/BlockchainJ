package com.ajlopez.blockchain.json;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonStringValue extends JsonValue {
    public JsonStringValue(String value) {
        super(ValueType.STRING, value);
    }

    @Override
    public String toString() {
        String value = (String)this.getValue();
        StringBuffer buffer = new StringBuffer(value.length() + 2);

        buffer.append('"');

        for (int k = 0; k < value.length(); k++) {
            char ch = value.charAt(k);

            if (ch == '\t')
                buffer.append("\\t");
            else if (ch == '\r')
                buffer.append("\\r");
            else if (ch == '\n')
                buffer.append("\\n");
            else if (ch == '"')
                buffer.append("\\\"");
            else if (ch == '\\')
                buffer.append("\\\\");
            else
                buffer.append(ch);
        }

        buffer.append('"');

        return buffer.toString();
    }
}

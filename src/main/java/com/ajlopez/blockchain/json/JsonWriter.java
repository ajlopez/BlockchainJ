package com.ajlopez.blockchain.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by ajlopez on 06/09/2019.
 */
public class JsonWriter {
    private final Writer writer;

    public JsonWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(JsonValue jsonValue) throws IOException {
        switch(jsonValue.getType()) {
            case OBJECT:
                this.writer.write("{");

                JsonObjectValue ojsonValue = (JsonObjectValue)jsonValue;

                int nproperties = 0;

                for (String name : ojsonValue.getPropertyNames()) {
                    if (nproperties > 0)
                        this.writer.write(",");

                    this.writer.write(" ");

                    this.writer.write("\"");
                    this.writer.write(name);
                    this.writer.write("\": ");

                    this.write(ojsonValue.getProperty(name));

                    nproperties++;
                }

                if (nproperties > 0)
                    this.writer.write(" ");

                this.writer.write("}");

                break;

            case ARRAY:
                this.writer.write("[");

                JsonArrayValue ajsonValue = (JsonArrayValue)jsonValue;

                int nelements = 0;

                for (JsonValue element : ajsonValue.getValues()) {
                    if (nelements > 0)
                        this.writer.write(",");

                    this.writer.write(" ");

                    this.write(element);

                    nelements++;
                }

                if (nelements > 0)
                    this.writer.write(" ");

                this.writer.write("]");

                break;

            default:
                this.writer.write(jsonValue.toString());
        }
    }
}

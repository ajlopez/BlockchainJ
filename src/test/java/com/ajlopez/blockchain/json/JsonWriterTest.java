package com.ajlopez.blockchain.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 08/09/2019.
 */
public class JsonWriterTest {
    @Test
    public void writeEmptyJsonObjectValue() throws IOException {
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        JsonObjectValue value = new JsonObjectValue(properties);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.write(value);

        writer.close();

        String result = writer.toString();

        Assert.assertEquals("{}", result);
    }

    @Test
    public void writeJsonObjectValueWithTwoProperties() throws IOException {
        JsonValue name = new JsonStringValue("adam");
        JsonValue age = new JsonNumericValue("900");
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        properties.put("name", name);
        properties.put("age", age);

        JsonObjectValue value = new JsonObjectValue(properties);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.write(value);

        writer.close();

        String result = writer.toString();

        Assert.assertEquals("{ \"name\": \"adam\", \"age\": 900 }", result);
    }

    @Test
    public void writeEmptyJsonArrayValue() throws IOException {
        List<JsonValue> values = new ArrayList<>();

        JsonArrayValue value = new JsonArrayValue(values);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.write(value);

        writer.close();

        String result = writer.toString();

        Assert.assertEquals("[]", result);
    }

    @Test
    public void writeJsonArrayValueWithTwoElements() throws IOException {
        JsonValue value1 = new JsonStringValue("foo");
        JsonValue value2 = new JsonNumericValue("42");
        List<JsonValue> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        JsonArrayValue value = new JsonArrayValue(values);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.write(value);

        writer.close();

        String result = writer.toString();

        Assert.assertEquals("[ \"foo\", 42 ]", result);
    }
}

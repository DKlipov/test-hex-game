package org.openjfx.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;

public class ResourceDeserializer<T> extends StdDeserializer<T> {

    private Class<T> vc;
    private Map<Class<?>, Map<String, ?>> data;

    public ResourceDeserializer(Class<T> vc, Map<Class<?>, Map<String, ?>> data) {
        super(vc);
        this.vc=vc;
        this.data = data;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        return (T)data.get(vc).get(jp.getText());
    }
}

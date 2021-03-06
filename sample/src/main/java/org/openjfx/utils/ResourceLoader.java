package org.openjfx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.openjfx.map.economy.production.template.FactoryType;
import org.openjfx.map.economy.production.template.ResourceGatheringType;
import org.openjfx.map.economy.production.template.SelfEmployedType;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class ResourceLoader {
    private final static Map<Class<?>, String> available = new LinkedHashMap<>() {{
        put(TradeGoodType.class, "trade-good-type.json");
        put(FactoryType.class, "factory-type.json");
        put(ResourceGatheringType.class, "gathering-type.json");
        put(SelfEmployedType.class, "self-employed-type.json");
    }};

    public final static Map<Class<?>, Map<String, ?>> resources = initResources();

    public <T> Map<String, T> getResources(Class<T> target) {
        return (Map) resources.get(target);
    }

    private Map<Class<?>, Map<String, ?>> initResources() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        var result = new HashMap<Class<?>, Map<String, ?>>();
        for (var e : available.entrySet()) {
            try {
                Map<String, Object> stage = new HashMap<>();
                String text = IOUtils.toString(ResourceLoader.class.getClassLoader()
                        .getResourceAsStream("templates/" + e.getValue()), "UTF-8");
                ObjectNode node = (ObjectNode) mapper.readTree(text);
                Iterator<String> it = node.fieldNames();
                while (it.hasNext()) {
                    var key = it.next();
                    ObjectNode t = (ObjectNode) node.get(key);
                    t.put("id", key);
                    stage.put(key, mapper.readValue(t.toString(), e.getKey()));
                }
                result.put(e.getKey(), stage);
                mapper = new ObjectMapper();
                module.addDeserializer(e.getKey(), new ResourceDeserializer(e.getKey(), result));
                mapper.registerModule(module);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }
}

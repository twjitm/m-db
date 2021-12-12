package com.mdb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author tangwenjiang
 *
 */
public class ZJsonUtils {


    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String dumps(Map<?, ?> object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    public static String dumps(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    public static Object loads(byte[] data, Class<?> t) {
        try {
            return MAPPER.readValue(data, t);
        } catch (IOException e) {
        }
        return null;
    }

    public static <T> T loads(String content, Class<T> t) {
        try {
            return MAPPER.readValue(content, t);
        } catch (IOException e) {
        }
        return null;
    }

    public static <T> T loads(String content, TypeReference<T> valueTypeRef) {
        try {
            return MAPPER.readValue(content, valueTypeRef);
        } catch (IOException e) {
        }
        return null;
    }

    public static JsonNode readTree(String content) {
        try {
            return MAPPER.readTree(content);
        } catch (IOException e) {
        }
        return null;
    }

    public static JsonNode valueToTree(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    /**
     * 解析json 数组
     */
    public static <T> List<T> loadArray(String content, Class<T> clazz) throws IOException {

        try {
            return MAPPER.readValue(content, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
        }
        return null;
    }

    public static Optional<Integer> getInt(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        JsonNode jsonNode = node.path(path);
        return jsonNode.isInt() ? Optional.of(jsonNode.asInt()) : Optional.empty();
    }

    public static Optional<List<Integer>> getIntList(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        JsonNode jsonNode = node.path(path);
        if (!jsonNode.isArray()) {
            return Optional.empty();
        }
        List<Integer> list = new ArrayList<>();
        for (JsonNode node1 : jsonNode) {
            if (node1.isInt()) {
                list.add(node1.asInt());
            }
        }
        return Optional.of(list);
    }

    public static Optional<Long> getLong(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        JsonNode jsonNode = node.path(path);
        return jsonNode.isNumber() ? Optional.of(jsonNode.asLong()) : Optional.empty();
    }

    public static Optional<List<Long>> getLongList(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        JsonNode jsonNode = node.path(path);
        if (!jsonNode.isArray()) {
            return Optional.empty();
        }
        List<Long> list = new ArrayList<>();
        for (JsonNode node1 : jsonNode) {
            if (node1.isNumber()) {
                list.add(node1.asLong());
            }
        }
        return Optional.of(list);
    }

    public static Optional<String> getString(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        return Optional.of(node.path(path).asText());
    }

    public static Optional<Boolean> getBoolean(JsonNode node, String path) {
        if (!node.hasNonNull(path)) {
            return Optional.empty();
        }
        JsonNode jsonNode = node.path(path);
        if (!jsonNode.isBoolean()) {
            return Optional.empty();
        }
        return Optional.of(jsonNode.asBoolean());
    }

}

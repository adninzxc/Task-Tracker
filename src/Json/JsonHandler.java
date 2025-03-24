package Json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonHandler {

    public static Object parseJson(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            return parseJsonObject(json);
        } else if (json.startsWith("[")) {
            return parseJsonArray(json);
        } else if (json.startsWith("\"") && json.endsWith("\"")) {
            return json.substring(1, json.length() - 1);
        } else if (json.equalsIgnoreCase("true") || json.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(json);
        } else if (json.matches("-?\\d+(\\.\\d+)?")) {
            return json.contains(".") ? Double.parseDouble(json) : Integer.parseInt(json);
        } else {
            throw new IllegalArgumentException("Invalid JSON value: " + json);
        }
    }

    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        json = json.substring(1, json.length() - 1).trim();
        String[] entries = splitJsonEntries(json);
        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] keyValue = entry.split(":", 2);
            String key = parseJson(keyValue[0].trim()).toString();
            Object value = parseJson(keyValue[1].trim());
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> parseJsonArray(String json) {
        List<Object> list = new ArrayList<>();
        json = json.substring(1, json.length() - 1).trim();
        String[] items = splitJsonEntries(json);
        for (String item : items) {
            if (item.isEmpty()) continue;
            list.add(parseJson(item.trim()));
        }
        return list;
    }

    private static String[] splitJsonEntries(String json) {
        List<String> entries = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                escaped = false;
                current.append(c);
                continue;
            }

            if (c == '\\') {
                escaped = true;
                current.append(c);
                continue;
            }

            if (c == '"') {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }

                if (c == ',' && depth == 0) {
                    entries.add(current.toString().trim());
                    current = new StringBuilder();
                    continue;
                }
            }

            current.append(c);
        }

        String lastEntry = current.toString().trim();
        if (!lastEntry.isEmpty()) {
            entries.add(lastEntry);
        }

        return entries.toArray(new String[0]);
    }

    public static String escapeJsonString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String toJsonString(String value) {
        return "\"" + escapeJsonString(value) + "\"";
    }

    public static String toJsonNumber(Number value) {
        return value.toString();
    }

    public static String toJsonBoolean(boolean value) {
        return value ? "true" : "false";
    }
}
package de.godly.iasimplifier.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlUtil {
    /**
     * Very important function to serialise Data as Bukkit/Spigot Configs do it, aka as ItemsAdder does it
     * @param input
     */
    public static void prepareInput(Map<String, Object> input) {
        List<String> keysToRemove = new ArrayList<>();
        Map<String, Object> output = new HashMap<>();

        input.forEach((key, value) -> {
            if (key.contains(".")) {
                keysToRemove.add(key);
                output.putAll(mergeValues(output, splitKey(key, value)));
            }
        });

        // Remove entries that have been converted into nested maps
        keysToRemove.forEach(key -> {
            input.remove(key);
        });

        // Add the nested maps
        output.forEach((key, value) -> {
            input.put(key, value);
        });
    }

    private static Map<String, Object> mergeValues(Map<String, Object> completeOutput, Map<String, Object> tempOutput) {
        Map.Entry<String, Object> entry = tempOutput.entrySet().stream().findFirst().get();
        Map<String, Object> output = new HashMap<>();
        output.putAll(completeOutput);

        if (completeOutput.containsKey(entry.getKey())) {
            output.put(entry.getKey(),
                    mergeValues(
                            (Map<String, Object>) completeOutput.get(entry.getKey()),
                            (Map<String, Object>) entry.getValue()
                    )
            );
        } else {
            output.put(entry.getKey(), entry.getValue());
        }

        return output;
    }

    private static Map<String, Object> splitKey(String key, Object value) {
        Map<String, Object> output = new HashMap<>();

        int dotIndex = key.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == key.length() - 1) {
            output.put(key, value);
        } else {
            String innerKey = key.substring(dotIndex + 1, key.length());
            Map<String, Object> innerObject = new HashMap<>();
            innerObject.put(innerKey, value);

            String outerKey = key.substring(0, dotIndex);
            output.putAll(splitKey(outerKey, innerObject));
        }

        return output;
    }
}

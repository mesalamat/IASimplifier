package de.godly.iasimplifier.util;

import java.util.Map;

public class MapUtil {
    public static void putIfNotNull(Map<String, Object> target, String key, Object value){
        if(value == null)return;
        target.put(key, value);
    }
}

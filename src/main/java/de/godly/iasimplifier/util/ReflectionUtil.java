package de.godly.iasimplifier.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectionUtil {



    @SneakyThrows
    public static Field getFieldOnClass(Object obj, String fieldName){
        return obj.getClass().getDeclaredField(fieldName);
    }


    @SneakyThrows
    public static Field[] getAllFields(Class obj){
        return obj.getDeclaredFields();
    }
    @SneakyThrows
    public static Field[] getAllFields(Object obj){
        return obj.getClass().getDeclaredFields();
    }

}

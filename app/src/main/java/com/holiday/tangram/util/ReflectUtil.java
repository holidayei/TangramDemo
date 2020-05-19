package com.holiday.tangram.util;

import java.lang.reflect.Field;

public class ReflectUtil {
    public static Object getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setFieldValueByFieldName(String fieldName, Object object, Object value) {
        try {
            Class c = object.getClass();
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(object, value);
        } catch (Exception e) {
        }
    }
}

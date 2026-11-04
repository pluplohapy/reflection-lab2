package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

final class Types {

    private static final Map<Class<?>, Class<?>> WRAPPERS = Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(short.class, Short.class),
            Map.entry(char.class, Character.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(float.class, Float.class),
            Map.entry(double.class, Double.class),
            Map.entry(void.class, Void.class));

    private Types() {
    }

    static Class<?> rawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof ParameterizedType parameterized) {
            return rawClass(parameterized.getRawType());
        }
        if (type instanceof GenericArrayType genericArray) {
            Class<?> component = rawClass(genericArray.getGenericComponentType());
            return Array.newInstance(component, 0).getClass();
        }
        if (type instanceof WildcardType wildcard) {
            Type[] bounds = wildcard.getUpperBounds();
            return bounds.length == 0 ? Object.class : rawClass(bounds[0]);
        }
        if (type instanceof TypeVariable<?> variable) {
            Type[] bounds = variable.getBounds();
            return bounds.length == 0 ? Object.class : rawClass(bounds[0]);
        }
        throw new GenerationException("Не удалось определить класс для типа " + type.getTypeName());
    }

    static Type typeArgument(Type type, int index, Type fallback) {
        if (type instanceof ParameterizedType parameterized) {
            Type[] arguments = parameterized.getActualTypeArguments();
            if (index < arguments.length) {
                return arguments[index];
            }
        }
        return fallback;
    }

    static boolean isSimple(Class<?> type) {
        return type.isPrimitive() || WRAPPERS.containsValue(type);
    }

    static Class<?> boxed(Class<?> type) {
        return WRAPPERS.getOrDefault(type, type);
    }

    static boolean isJdkType(Class<?> type) {
        String name = type.getName();
        return name.startsWith("java.") || name.startsWith("javax.");
    }

    static boolean isConcrete(Class<?> type) {
        return !type.isPrimitive()
                && !type.isArray()
                && !type.isInterface()
                && !type.isAnnotation()
                && !type.isEnum()
                && !Modifier.isAbstract(type.getModifiers());
    }
}

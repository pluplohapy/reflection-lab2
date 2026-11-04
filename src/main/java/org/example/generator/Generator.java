package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Generator {

    private static final int MAX_DEPTH = 5;

    private static final int MAX_ATTEMPTS = 1000;

    private final Random random;

    private final List<Class<?>> implementations = new ArrayList<>();

    public Generator() {
        this(new Random().nextLong());
    }

    public Generator(long seed) {
        this.random = new Random(seed);
        this.implementations.addAll(Implementations.findGeneratableClasses());
    }

    public <T> T generateValueOfType(Class<T> type) {
        Objects.requireNonNull(type, "type не может быть null");
        Object value = generate(type, 0);
        if (value == null) {
            throw new GenerationException("Не удалось сгенерировать значение типа " + type.getName());
        }
        @SuppressWarnings("unchecked")
        T result = (T) value;
        return result;
    }

    public Generator register(Class<?>... types) {
        for (Class<?> type : types) {

            if (!Types.isConcrete(type)) {
                throw new IllegalArgumentException("Можно зарегистрировать только конкретный класс "
                        + "(не примитив, не массив, не интерфейс, не абстрактный класс и не enum), а получен: "
                        + type.getName());
            }
            if (!implementations.contains(type)) {
                implementations.add(type);
            }
        }
        return this;
    }

    private Object generate(Type type, int depth) {
        Class<?> raw = Types.rawClass(type);

        if (raw == void.class || raw == Void.class) {
            throw new GenerationException("Тип " + raw.getName() + " не может быть сгенерирован");
        }

        if (Types.isSimple(raw)) {
            return Values.scalar(raw, random);
        }

        if (raw == String.class || raw == CharSequence.class) {
            return Values.string(random);
        }

        if (raw.isEnum()) {
            return Values.enumValue(raw, random);
        }

        if (raw == Object.class) {
            return Values.simpleValue(random);
        }

        if (depth >= MAX_DEPTH) {
            return null;
        }

        if (raw.isArray()) {
            return createArray(type, raw, depth);
        }

        if (Map.class.isAssignableFrom(raw)) {
            return createMap(type, raw, depth);
        }

        if (Collection.class.isAssignableFrom(raw) || raw == Iterable.class) {
            return createList(type, raw, depth);
        }

        if (raw.isInterface() || Modifier.isAbstract(raw.getModifiers())) {
            return createAbstract(raw, depth);
        }

        if (Types.isJdkType(raw)) {
            throw new GenerationException("Класс " + raw.getName() + " не поддерживается генератором");
        }

        return createObject(raw, depth);
    }

    private Object createArray(Type type, Class<?> raw, int depth) {

        Type componentType = type instanceof GenericArrayType genericArray
                ? genericArray.getGenericComponentType()
                : raw.getComponentType();

        Class<?> componentClass = raw.getComponentType();
        int length = Values.arrayLength(random);
        Object array = Array.newInstance(componentClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, generate(componentType, depth + 1));
        }
        return array;
    }

    private Object createList(Type type, Class<?> raw, int depth) {

        if (!raw.isAssignableFrom(ArrayList.class)) {
            throw new GenerationException("Коллекция " + raw.getName()
                    + " не поддерживается: используются только списки");
        }
        List<Object> list = new ArrayList<>();

        Type elementType = Types.typeArgument(type, 0, Object.class);
        int size = Values.collectionSize(random);
        for (int i = 0; i < size; i++) {
            Object element = generate(elementType, depth + 1);
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    private Object createMap(Type type, Class<?> raw, int depth) {

        if (!raw.isAssignableFrom(LinkedHashMap.class)) {
            throw new GenerationException("Отображение " + raw.getName()
                    + " не поддерживается: используется LinkedHashMap");
        }
        Map<Object, Object> map = new LinkedHashMap<>();

        Type keyType = Types.typeArgument(type, 0, String.class);
        Type valueType = Types.typeArgument(type, 1, Object.class);
        int size = Values.collectionSize(random);
        int attempts = 0;
        while (map.size() < size && attempts++ < size * 5) {
            Object key = generate(keyType, depth + 1);
            Object value = generate(valueType, depth + 1);
            if (key != null && value != null && !map.containsKey(key)) {
                map.put(key, value);
            }
        }
        return map;
    }

    private Object createAbstract(Class<?> raw, int depth) {
        List<Class<?>> candidates = new ArrayList<>();
        for (Class<?> implementation : implementations) {

            if (implementation != raw && raw.isAssignableFrom(implementation)) {
                candidates.add(implementation);
            }
        }

        if (candidates.isEmpty()) {
            throw new GenerationException("Нет реализации для " + raw.getName()
                    + ": пометьте класс аннотацией @Generatable или зарегистрируйте его через register(...)");
        }
        Collections.shuffle(candidates, random);
        for (Class<?> candidate : candidates) {
            try {
                return createObject(candidate, depth);
            } catch (GenerationException ignored) {

            }
        }
        throw new GenerationException("Ни одну реализацию " + raw.getName() + " не удалось создать");
    }

    private Object createObject(Class<?> type, int depth) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            Object instance = instantiate(type, depth);
            fillFields(type, instance, depth);

            if (!(instance instanceof SelfValidating validating) || validating.isValid()) {
                return instance;
            }
        }
        throw new GenerationException("Не удалось создать валидный объект " + type.getName()
                + " за " + MAX_ATTEMPTS + " попыток");
    }

    private Object instantiate(Class<?> type, int depth) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();

        Arrays.sort(constructors, Comparator
                .comparingInt((Constructor<?> constructor) -> constructor.getParameterCount())
                .thenComparingInt(constructor -> Modifier.isPublic(constructor.getModifiers()) ? 0 : 1));
        List<String> failures = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isSynthetic()) {
                continue;
            }
            try {
                constructor.setAccessible(true);
                Object[] arguments = arguments(constructor, depth);
                return constructor.newInstance(arguments);
            } catch (InvocationTargetException e) {
                failures.add(describe(constructor) + " -> " + e.getCause());
            } catch (ReflectiveOperationException | RuntimeException e) {
                failures.add(describe(constructor) + " -> " + e.getMessage());
            }
        }
        throw new GenerationException("Не удалось создать " + type.getName()
                + ": " + String.join("; ", failures));
    }

    private Object[] arguments(Constructor<?> constructor, int depth) {
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = generate(parameterTypes[i], depth + 1);
        }
        return arguments;
    }

    private void fillFields(Class<?> type, Object instance, int depth) {

        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            if (current != type && Types.isJdkType(current)) {
                break;
            }
            for (Field field : current.getDeclaredFields()) {
                int modifiers = field.getModifiers();

                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || field.isSynthetic()) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object value = generate(field.getGenericType(), depth + 1);
                    if (value != null) {
                        field.set(instance, value);
                    }
                } catch (RuntimeException | IllegalAccessException ignored) {

                }
            }
        }
    }

    private String describe(Constructor<?> constructor) {
        StringBuilder builder = new StringBuilder(constructor.getDeclaringClass().getSimpleName()).append('(');
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterTypes[i].getSimpleName());
        }
        return builder.append(')').toString();
    }
}

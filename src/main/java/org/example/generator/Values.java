package org.example.generator;

import java.util.Random;

final class Values {

    private static final int MIN_INT = -1000;
    private static final int MAX_INT = 1000;

    private static final double MIN_DOUBLE = -1000.0d;
    private static final double MAX_DOUBLE = 1000.0d;

    private static final int MIN_STRING_LENGTH = 4;
    private static final int MAX_STRING_LENGTH = 12;

    private static final int MIN_ARRAY_LENGTH = 1;
    private static final int MAX_ARRAY_LENGTH = 5;
    private static final int MIN_COLLECTION_SIZE = 1;
    private static final int MAX_COLLECTION_SIZE = 5;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private Values() {
    }

    static Object scalar(Class<?> type, Random random) {
        Class<?> boxed = Types.boxed(type);
        if (boxed == Boolean.class) {
            return random.nextBoolean();
        }
        if (boxed == Byte.class) {
            return (byte) (random.nextInt(256) - 128);
        }
        if (boxed == Short.class) {
            return (short) (random.nextInt(65_536) - 32_768);
        }
        if (boxed == Integer.class) {
            return randomInt(random);
        }
        if (boxed == Long.class) {
            return (long) randomInt(random);
        }
        if (boxed == Float.class) {
            return (float) randomDouble(random);
        }
        if (boxed == Double.class) {
            return randomDouble(random);
        }
        if (boxed == Character.class) {
            return randomChar(random);
        }
        throw new GenerationException("Нет правила для типа " + type.getName());
    }

    static int randomInt(Random random) {
        return MIN_INT + random.nextInt(MAX_INT - MIN_INT + 1);
    }

    static double randomDouble(Random random) {
        double value = MIN_DOUBLE + random.nextDouble() * (MAX_DOUBLE - MIN_DOUBLE);
        return Math.round(value * 100.0d) / 100.0d;
    }

    static char randomChar(Random random) {
        return (char) (' ' + random.nextInt('~' - ' ' + 1));
    }

    static String string(Random random) {
        int length = between(random, MIN_STRING_LENGTH, MAX_STRING_LENGTH);
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    static Object enumValue(Class<?> type, Random random) {
        Object[] constants = type.getEnumConstants();
        if (constants == null || constants.length == 0) {
            throw new GenerationException("У перечисления " + type.getName() + " нет констант");
        }
        return constants[random.nextInt(constants.length)];
    }

    static Object simpleValue(Random random) {
        return switch (random.nextInt(5)) {
            case 0 -> randomInt(random);
            case 1 -> string(random);
            case 2 -> random.nextBoolean();
            case 3 -> randomDouble(random);
            default -> randomChar(random);
        };
    }

    static int arrayLength(Random random) {
        return between(random, MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH);
    }

    static int collectionSize(Random random) {
        return between(random, MIN_COLLECTION_SIZE, MAX_COLLECTION_SIZE);
    }

    private static int between(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}

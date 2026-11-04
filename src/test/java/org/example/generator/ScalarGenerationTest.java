package org.example.generator;

import org.example.classes.Color;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScalarGenerationTest {

    private final Generator generator = new Generator(1L);

    @Test
    void generatesAllPrimitiveTypes() {
        assertInstanceOf(Boolean.class, generator.generateValueOfType(boolean.class));
        assertInstanceOf(Byte.class, generator.generateValueOfType(byte.class));
        assertInstanceOf(Short.class, generator.generateValueOfType(short.class));
        assertInstanceOf(Character.class, generator.generateValueOfType(char.class));
        assertInstanceOf(Integer.class, generator.generateValueOfType(int.class));
        assertInstanceOf(Long.class, generator.generateValueOfType(long.class));
        assertInstanceOf(Float.class, generator.generateValueOfType(float.class));
        assertInstanceOf(Double.class, generator.generateValueOfType(double.class));
    }

    @Test
    void generatesAllWrapperTypes() {
        assertNotNull(generator.generateValueOfType(Boolean.class));
        assertNotNull(generator.generateValueOfType(Byte.class));
        assertNotNull(generator.generateValueOfType(Short.class));
        assertNotNull(generator.generateValueOfType(Character.class));
        assertNotNull(generator.generateValueOfType(Integer.class));
        assertNotNull(generator.generateValueOfType(Long.class));
        assertNotNull(generator.generateValueOfType(Float.class));
        assertNotNull(generator.generateValueOfType(Double.class));
    }

    @Test
    void integersStayInsideConfiguredRange() {
        for (int i = 0; i < 200; i++) {
            int value = generator.generateValueOfType(int.class);
            assertTrue(value >= -1000 && value <= 1000, "значение вне диапазона: " + value);
        }
    }

    @Test
    void doublesStayInsideConfiguredRange() {
        for (int i = 0; i < 200; i++) {
            double value = generator.generateValueOfType(double.class);
            assertTrue(value >= -1000.0 && value <= 1000.0, "значение вне диапазона: " + value);
        }
    }

    @Test
    void charactersArePrintable() {
        for (int i = 0; i < 100; i++) {
            char value = generator.generateValueOfType(char.class);
            assertTrue(value >= ' ' && value <= '~', "символ не печатный: " + value);
        }
    }

    @Test
    void stringsAreNotEmptyAndVary() {
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String value = generator.generateValueOfType(String.class);
            assertNotNull(value);
            assertTrue(value.length() >= 4 && value.length() <= 12, "длина строки вне диапазона: " + value.length());
            values.add(value);
        }
        assertTrue(values.size() > 1, "строки не должны быть одинаковыми");
    }

    @Test
    void enumsReturnDeclaredConstants() {
        for (int i = 0; i < 100; i++) {
            Color color = generator.generateValueOfType(Color.class);
            assertTrue(EnumSet.allOf(Color.class).contains(color));
        }
    }

    @Test
    void allEnumConstantsEventuallyAppear() {
        Set<Color> seen = EnumSet.noneOf(Color.class);
        for (int i = 0; i < 300; i++) {
            seen.add(generator.generateValueOfType(Color.class));
        }
        assertEquals(EnumSet.allOf(Color.class), seen);
    }

    @Test
    void valuesAreRandom() {
        Set<Integer> values = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            values.add(generator.generateValueOfType(int.class));
        }
        assertTrue(values.size() > 1, "генератор не должен возвращать константу");
    }
}

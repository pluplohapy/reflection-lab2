package org.example.generator;

import org.example.classes.Color;
import org.example.classes.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayGenerationTest {

    private final Generator generator = new Generator(2L);

    @Test
    void generatesPrimitiveArrays() {
        int[] numbers = generator.generateValueOfType(int[].class);
        assertNotNull(numbers);
        assertTrue(numbers.length >= 1 && numbers.length <= 5);

        double[] decimals = generator.generateValueOfType(double[].class);
        assertNotNull(decimals);
        assertTrue(decimals.length >= 1);

        boolean[] flags = generator.generateValueOfType(boolean[].class);
        assertNotNull(flags);
        assertTrue(flags.length >= 1);
    }

    @Test
    void generatesStringArraysWithoutNulls() {
        String[] words = generator.generateValueOfType(String[].class);
        assertNotNull(words);
        for (String word : words) {
            assertNotNull(word);
        }
    }

    @Test
    void generatesEnumArrays() {
        Color[] colors = generator.generateValueOfType(Color[].class);
        assertNotNull(colors);
        for (Color color : colors) {
            assertNotNull(color);
        }
    }

    @Test
    void generatesArraysOfUserDefinedObjects() {
        Rectangle[] rectangles = generator.generateValueOfType(Rectangle[].class);
        assertNotNull(rectangles);
        for (Rectangle rectangle : rectangles) {
            assertNotNull(rectangle);
            assertTrue(rectangle.isValid(), "прямоугольник должен проходить проверку");
        }
    }

    @Test
    void generatesMultidimensionalArrays() {
        int[][] matrix = generator.generateValueOfType(int[][].class);
        assertNotNull(matrix);
        for (int[] row : matrix) {
            assertNotNull(row);
            assertTrue(row.length >= 1);
        }
    }

    @Test
    void arraysAreFilledIndependently() {
        int[] first = generator.generateValueOfType(int[].class);
        int[] second = generator.generateValueOfType(int[].class);

        assertTrue(!Arrays.equals(first, second) || first.length != second.length,
                "два массива оказались одинаковыми");
    }
}

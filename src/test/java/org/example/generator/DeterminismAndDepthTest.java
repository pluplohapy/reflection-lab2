package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.example.testmodel.CyclicA;
import org.example.testmodel.CyclicB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeterminismAndDepthTest {

    @Test
    void sameSeedProducesTheSameValues() {
        Generator first = new Generator(42L);
        Generator second = new Generator(42L);
        for (int i = 0; i < 20; i++) {
            assertEquals(first.generateValueOfType(int.class), second.generateValueOfType(int.class));
            assertEquals(first.generateValueOfType(String.class), second.generateValueOfType(String.class));
            assertEquals(first.generateValueOfType(List.class), second.generateValueOfType(List.class));
        }
    }

    @Test
    void differentSeedsProduceDifferentValues() {
        Generator first = new Generator(1L);
        Generator second = new Generator(2L);
        boolean differs = false;
        for (int i = 0; i < 20 && !differs; i++) {
            differs = !first.generateValueOfType(int.class).equals(second.generateValueOfType(int.class));
        }
        assertTrue(differs, "разные seed должны давать разные значения");
    }

    @Test
    @Timeout(10)
    void mutuallyRecursiveClassesAreDepthLimited() {
        CyclicA start = new Generator(13L).generateValueOfType(CyclicA.class);
        assertNotNull(start);
        assertTrue(chainLength(start) <= 5, "цепочка объектов должна быть ограничена глубиной");
    }

    @Test
    @Timeout(20)
    void deepGenerationDoesNotOverflow() {
        Generator generator = new Generator(17L);
        for (int i = 0; i < 200; i++) {
            assertNotNull(generator.generateValueOfType(BinaryTreeNode.class));
            assertNotNull(generator.generateValueOfType(CyclicA.class));
        }
    }

    private static int chainLength(CyclicA start) {
        int length = 0;
        CyclicA current = start;
        while (current != null && length < 50) {
            length++;
            CyclicB next = current.getB();
            if (next == null) {
                break;
            }
            length++;
            current = next.getA();
        }
        return length;
    }
}

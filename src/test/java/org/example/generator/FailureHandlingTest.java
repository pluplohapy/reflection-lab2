package org.example.generator;

import org.example.testmodel.BrokenConstructor;
import org.example.testmodel.Courier;
import org.example.testmodel.NeverValid;
import org.example.testmodel.NoUsableConstructor;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailureHandlingTest {

    private final Generator generator = new Generator(8L);

    @Test
    void nullTypeIsRejected() {
        assertThrows(NullPointerException.class, () -> generator.generateValueOfType((Class<Object>) null));
    }

    @Test
    void voidTypesAreRejected() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(void.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Void.class));
    }

    @Test
    void jdkTypesAreRejected() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Thread.class));
        assertTrue(error.getMessage().contains("Thread"));
    }

    @Test
    void unsupportedCollectionsAreRejected() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(TreeSet.class));
    }

    @Test
    void interfaceWithoutImplementationIsReported() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Courier.class));
        assertTrue(error.getMessage().contains("Courier"));
        assertTrue(error.getMessage().contains("@Generatable"));
    }

    @Test
    void brokenConstructorIsReportedWithItsCause() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(BrokenConstructor.class));
        assertTrue(error.getMessage().contains("BrokenConstructor"));
        assertTrue(error.getMessage().contains("boom"));
    }

    @Test
    void unsupportedConstructorParameterIsReported() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(NoUsableConstructor.class));
        assertTrue(error.getMessage().contains("Thread"));
    }

    @Test
    void impossibleConstraintIsReported() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(NeverValid.class));
        assertTrue(error.getMessage().contains("1000"), "сообщение должно упоминать число попыток");
        assertTrue(error.getMessage().contains("NeverValid"));
    }

    @Test
    void everyFailureHasANonBlankMessage() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Courier.class));
        assertNotNull(error.getMessage());
        assertFalse(error.getMessage().isBlank());
    }
}

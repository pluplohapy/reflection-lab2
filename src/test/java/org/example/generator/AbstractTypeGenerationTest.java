package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.testmodel.Animal;
import org.example.testmodel.Dog;
import org.example.testmodel.PlainAnimal;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractTypeGenerationTest {

    private final Generator generator = new Generator(5L);

    @Test
    void shapeRequestReturnsAnImplementation() {
        for (int i = 0; i < 50; i++) {
            Shape shape = generator.generateValueOfType(Shape.class);
            assertNotNull(shape);
            assertTrue(shape.getArea() > 0);
            assertTrue(shape.getPerimeter() > 0);
            assertTrue(shape instanceof Rectangle || shape instanceof Triangle);
        }
    }

    @Test
    void bothShapeImplementationsAreUsed() {
        Set<Class<?>> types = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            types.add(generator.generateValueOfType(Shape.class).getClass());
        }
        assertEquals(Set.of(Rectangle.class, Triangle.class), types);
    }

    @Test
    void abstractClassUsesAnnotatedSubclass() {
        Animal animal = generator.generateValueOfType(Animal.class);
        assertInstanceOf(Dog.class, animal);
        assertNotNull(animal.getName());
        assertEquals("woof", animal.sound());
    }

    @Test
    void abstractClassWithoutImplementationsFails() {
        GenerationException error = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(PlainAnimal.class));
        assertTrue(error.getMessage().contains("@Generatable"));
    }
}

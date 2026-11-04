package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.testmodel.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainConstraintTest {

    private final Generator generator = new Generator(9L);

    @Test
    void rectanglesAlwaysHavePositiveSides() {
        for (int i = 0; i < 100; i++) {
            Rectangle rectangle = generator.generateValueOfType(Rectangle.class);
            assertTrue(rectangle.isValid());
            assertTrue(rectangle.getLength() > 0);
            assertTrue(rectangle.getWidth() > 0);
            assertTrue(rectangle.getArea() > 0);
        }
    }

    @Test
    void trianglesAlwaysSatisfyTheTriangleInequality() {
        for (int i = 0; i < 100; i++) {
            Triangle triangle = generator.generateValueOfType(Triangle.class);
            assertTrue(triangle.isValid());
            assertTrue(triangle.getSideA() + triangle.getSideB() > triangle.getSideC());
            assertTrue(triangle.getSideB() + triangle.getSideC() > triangle.getSideA());
            assertTrue(triangle.getSideA() + triangle.getSideC() > triangle.getSideB());
            assertTrue(triangle.getArea() > 0);
            assertTrue(Double.isFinite(triangle.getArea()));
        }
    }

    @Test
    void everyGeneratedShapeIsValid() {
        for (int i = 0; i < 50; i++) {
            Shape shape = generator.generateValueOfType(Shape.class);
            assertTrue(shape.getArea() > 0, "площадь должна быть положительной: " + shape);
            assertTrue(shape.getPerimeter() > 0, "периметр должен быть положительным: " + shape);
        }
    }

    @Test
    void constraintAlsoAppliesToDirectlyRequestedClass() {
        Square square = generator.generateValueOfType(Square.class);
        assertTrue(square.isValid());
        assertEquals(square.getSide() * square.getSide(), square.getArea(), 1e-9);
    }
}
